"""
AgriScan AI – Real MobileNetV2 Training Script
Uses TensorFlow Datasets (tfds) to download the actual PlantVillage dataset.

Usage:
    pip install tensorflow tensorflow-datasets
    python train.py

The dataset (~800MB) will be auto-downloaded on first run.
Training output: agriscan_model.h5 + classes.json
"""

import os
import json
import numpy as np
import tensorflow as tf
import tensorflow_datasets as tfds
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.layers import (Dense, GlobalAveragePooling2D,
                                     Dropout, BatchNormalization)
from tensorflow.keras.models import Model
from tensorflow.keras.callbacks import (ModelCheckpoint, EarlyStopping,
                                        ReduceLROnPlateau, TensorBoard)
from tensorflow.keras.optimizers import Adam
import matplotlib.pyplot as plt

# ─── Config ──────────────────────────────────────────────────────────────────
IMG_SIZE      = 224
BATCH_SIZE    = 32
EPOCHS_PHASE1 = 15   # Train head only
EPOCHS_PHASE2 = 10   # Fine-tune top layers
FINE_TUNE_AT  = 100  # Unfreeze layers from this index onwards

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH  = os.path.join(SCRIPT_DIR, 'agriscan_model.h5')
CLASSES_PATH = os.path.join(SCRIPT_DIR, 'classes.json')
LOG_DIR     = os.path.join(SCRIPT_DIR, 'logs')

print(f"TensorFlow version: {tf.__version__}")
print(f"GPU available: {len(tf.config.list_physical_devices('GPU')) > 0}")


# ─── Data Loading ────────────────────────────────────────────────────────────
def load_dataset():
    print("\n📥 Loading PlantVillage dataset via tensorflow_datasets...")
    print("   (First run will download ~800MB – subsequent runs use cache)\n")

    (ds_train, ds_val, ds_test), info = tfds.load(
        'plant_village',
        split=['train[:75%]', 'train[75%:90%]', 'train[90%:]'],
        as_supervised=True,
        with_info=True,
        shuffle_files=True,
    )

    num_classes = info.features['label'].num_classes
    class_names = info.features['label'].names
    total_samples = info.splits['train'].num_examples

    print(f"✅ Dataset loaded: {total_samples} total images, {num_classes} classes")
    return ds_train, ds_val, ds_test, num_classes, class_names


# ─── Preprocessing ───────────────────────────────────────────────────────────
def preprocess_image(image, label):
    """Resize and normalize image to MobileNetV2 input format."""
    image = tf.image.resize(image, (IMG_SIZE, IMG_SIZE))
    image = tf.cast(image, tf.float32)
    image = tf.keras.applications.mobilenet_v2.preprocess_input(image)
    return image, label


def augment_image(image, label):
    """Apply data augmentation for training robustness."""
    image = tf.image.random_flip_left_right(image)
    image = tf.image.random_flip_up_down(image)
    image = tf.image.random_brightness(image, max_delta=0.2)
    image = tf.image.random_contrast(image, lower=0.8, upper=1.2)
    image = tf.image.random_saturation(image, lower=0.8, upper=1.2)
    image = tf.image.random_hue(image, max_delta=0.05)

    # Random zoom (crop and resize)
    boxes = tf.constant([[0.05, 0.05, 0.95, 0.95]])
    box_indices = tf.zeros([1], dtype=tf.int32)
    image = tf.image.crop_and_resize(
        tf.expand_dims(image, 0), boxes, box_indices, [IMG_SIZE, IMG_SIZE]
    )
    image = tf.squeeze(image, axis=0)
    return image, label


def prepare_dataset(ds, batch_size, augment=False, shuffle_buffer=2000):
    """Prepare dataset pipeline with optional augmentation."""
    if augment:
        ds = ds.map(augment_image, num_parallel_calls=tf.data.AUTOTUNE)
    ds = ds.map(preprocess_image, num_parallel_calls=tf.data.AUTOTUNE)
    if augment:
        ds = ds.shuffle(shuffle_buffer)
    ds = ds.batch(batch_size)
    ds = ds.prefetch(tf.data.AUTOTUNE)
    return ds


# ─── Model Architecture ──────────────────────────────────────────────────────
def build_model(num_classes):
    """Build MobileNetV2 transfer learning model."""
    print("\n🏗️  Building MobileNetV2 transfer learning model...")

    base_model = MobileNetV2(
        input_shape=(IMG_SIZE, IMG_SIZE, 3),
        include_top=False,
        weights='imagenet'
    )
    base_model.trainable = False  # Freeze for Phase 1

    # Custom classification head
    inputs = tf.keras.Input(shape=(IMG_SIZE, IMG_SIZE, 3))
    x = base_model(inputs, training=False)
    x = GlobalAveragePooling2D()(x)
    x = BatchNormalization()(x)
    x = Dropout(0.3)(x)
    x = Dense(512, activation='relu')(x)
    x = BatchNormalization()(x)
    x = Dropout(0.2)(x)
    x = Dense(256, activation='relu')(x)
    x = Dropout(0.1)(x)
    outputs = Dense(num_classes, activation='softmax')(x)

    model = Model(inputs, outputs)

    print(f"✅ Model built: {base_model.count_params():,} base params")
    print(f"   Total trainable params (Phase 1): "
          f"{sum(tf.size(w).numpy() for w in model.trainable_weights):,}")

    return model, base_model


# ─── Training ────────────────────────────────────────────────────────────────
def get_callbacks(phase):
    """Get training callbacks."""
    return [
        ModelCheckpoint(
            MODEL_PATH,
            save_best_only=True,
            monitor='val_accuracy',
            mode='max',
            verbose=1
        ),
        EarlyStopping(
            monitor='val_accuracy',
            patience=5,
            restore_best_weights=True,
            verbose=1
        ),
        ReduceLROnPlateau(
            monitor='val_loss',
            factor=0.5,
            patience=3,
            min_lr=1e-7,
            verbose=1
        ),
        TensorBoard(log_dir=os.path.join(LOG_DIR, f'phase{phase}')),
    ]


def plot_history(history, phase_name):
    """Plot training curves."""
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))
    ax1.plot(history.history['accuracy'], label='Train Accuracy')
    ax1.plot(history.history['val_accuracy'], label='Val Accuracy')
    ax1.set_title(f'{phase_name} – Accuracy')
    ax1.legend()
    ax1.grid(True)

    ax2.plot(history.history['loss'], label='Train Loss')
    ax2.plot(history.history['val_loss'], label='Val Loss')
    ax2.set_title(f'{phase_name} – Loss')
    ax2.legend()
    ax2.grid(True)

    plt.tight_layout()
    path = os.path.join(SCRIPT_DIR, f'training_{phase_name.lower().replace(" ", "_")}.png')
    plt.savefig(path)
    print(f"📊 Training plot saved: {path}")
    plt.close()


# ─── Main ─────────────────────────────────────────────────────────────────────
def main():
    os.makedirs(LOG_DIR, exist_ok=True)

    # Load dataset
    ds_train_raw, ds_val_raw, ds_test_raw, num_classes, class_names = load_dataset()

    # Prepare datasets
    ds_train = prepare_dataset(ds_train_raw, BATCH_SIZE, augment=True)
    ds_val   = prepare_dataset(ds_val_raw,   BATCH_SIZE, augment=False)
    ds_test  = prepare_dataset(ds_test_raw,  BATCH_SIZE, augment=False)

    # Save class names
    with open(CLASSES_PATH, 'w') as f:
        json.dump(class_names, f, indent=2)
    print(f"\n✅ Class names saved to {CLASSES_PATH}")
    print(f"   Classes ({num_classes}): {class_names}")

    # Build model
    model, base_model = build_model(num_classes)

    # ── Phase 1: Train classification head ──────────────────────────────────
    print("\n" + "═"*60)
    print("PHASE 1: Training classification head (base frozen)")
    print("═"*60)

    model.compile(
        optimizer=Adam(learning_rate=1e-3),
        loss='sparse_categorical_crossentropy',
        metrics=['accuracy', tf.keras.metrics.TopKCategoricalAccuracy(k=3, name='top3_accuracy')]
    )

    history1 = model.fit(
        ds_train,
        epochs=EPOCHS_PHASE1,
        validation_data=ds_val,
        callbacks=get_callbacks(1),
        verbose=1
    )
    plot_history(history1, 'Phase 1 - Head Training')

    val_acc_p1 = max(history1.history['val_accuracy'])
    print(f"\n✅ Phase 1 complete – Best val accuracy: {val_acc_p1:.4f} ({val_acc_p1*100:.2f}%)")

    # ── Phase 2: Fine-tune top layers ───────────────────────────────────────
    print("\n" + "═"*60)
    print(f"PHASE 2: Fine-tuning (unfreezing from layer {FINE_TUNE_AT})")
    print("═"*60)

    base_model.trainable = True
    for layer in base_model.layers[:FINE_TUNE_AT]:
        layer.trainable = False

    trainable_count = sum(1 for l in base_model.layers if l.trainable)
    print(f"   Unfrozen layers: {trainable_count}/{len(base_model.layers)}")

    model.compile(
        optimizer=Adam(learning_rate=1e-5),  # Lower LR for fine-tuning
        loss='sparse_categorical_crossentropy',
        metrics=['accuracy', tf.keras.metrics.TopKCategoricalAccuracy(k=3, name='top3_accuracy')]
    )

    history2 = model.fit(
        ds_train,
        epochs=EPOCHS_PHASE2,
        validation_data=ds_val,
        callbacks=get_callbacks(2),
        verbose=1
    )
    plot_history(history2, 'Phase 2 - Fine Tuning')

    val_acc_p2 = max(history2.history['val_accuracy'])
    print(f"\n✅ Phase 2 complete – Best val accuracy: {val_acc_p2:.4f} ({val_acc_p2*100:.2f}%)")

    # ── Final Evaluation ─────────────────────────────────────────────────────
    print("\n" + "═"*60)
    print("FINAL EVALUATION on Test Set")
    print("═"*60)

    test_loss, test_acc, test_top3 = model.evaluate(ds_test, verbose=1)
    print(f"\n🎯 Test Accuracy:      {test_acc:.4f} ({test_acc*100:.2f}%)")
    print(f"🎯 Top-3 Accuracy:     {test_top3:.4f} ({test_top3*100:.2f}%)")
    print(f"📉 Test Loss:          {test_loss:.4f}")

    # Save final model
    model.save(MODEL_PATH)
    print(f"\n💾 Model saved: {MODEL_PATH}")

    # Save training summary
    summary = {
        "model": "MobileNetV2 Transfer Learning",
        "num_classes": num_classes,
        "class_names": class_names,
        "img_size": IMG_SIZE,
        "batch_size": BATCH_SIZE,
        "phase1_epochs": EPOCHS_PHASE1,
        "phase2_epochs": EPOCHS_PHASE2,
        "best_val_accuracy_phase1": float(val_acc_p1),
        "best_val_accuracy_phase2": float(val_acc_p2),
        "test_accuracy": float(test_acc),
        "test_top3_accuracy": float(test_top3),
        "test_loss": float(test_loss),
        "dataset": "PlantVillage (tensorflow_datasets)",
    }
    with open(os.path.join(SCRIPT_DIR, 'training_summary.json'), 'w') as f:
        json.dump(summary, f, indent=2)

    print("\n🎉 Training complete! Model is ready for deployment.")
    print(f"   Model: {MODEL_PATH}")
    print(f"   Classes: {CLASSES_PATH}")


if __name__ == '__main__':
    main()
