"""
AgriScan AI – ML Inference Engine
Loads the trained MobileNetV2 model and runs prediction on crop leaf images.
"""

import os
import json
import numpy as np
import tensorflow as tf
from PIL import Image
import io

IMG_SIZE = 224
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))

# Singleton model holder
_model = None
_class_names = None
_disease_info = None


def _load_resources():
    global _model, _class_names, _disease_info

    model_path   = os.path.join(SCRIPT_DIR, 'agriscan_model.h5')
    classes_path = os.path.join(SCRIPT_DIR, 'classes.json')
    disease_path = os.path.join(SCRIPT_DIR, 'disease_info.json')

    if _model is None:
        if not os.path.exists(model_path):
            raise FileNotFoundError(
                f"Model not found at {model_path}. "
                "Please run ml/train.py first to train the model."
            )
        print("Loading AgriScan model...")
        _model = tf.keras.models.load_model(model_path)
        print("Model loaded successfully.")

    if _class_names is None:
        with open(classes_path, 'r') as f:
            _class_names = json.load(f)

    if _disease_info is None:
        with open(disease_path, 'r') as f:
            _disease_info = json.load(f)

    return _model, _class_names, _disease_info


def preprocess_image(image_input):
    """
    Preprocess image for MobileNetV2 inference.
    Accepts: file path (str), bytes, PIL Image, or file-like object.
    Returns: numpy array of shape (1, 224, 224, 3)
    """
    if isinstance(image_input, str):
        img = Image.open(image_input)
    elif isinstance(image_input, bytes):
        img = Image.open(io.BytesIO(image_input))
    elif hasattr(image_input, 'read'):
        img = Image.open(image_input)
    elif isinstance(image_input, Image.Image):
        img = image_input
    else:
        raise ValueError("Unsupported image input type")

    # Convert to RGB (handles RGBA, grayscale, etc.)
    img = img.convert('RGB')

    # Resize
    img = img.resize((IMG_SIZE, IMG_SIZE), Image.LANCZOS)

    # To numpy
    arr = np.array(img, dtype=np.float32)

    # MobileNetV2 preprocessing: scale to [-1, 1]
    arr = tf.keras.applications.mobilenet_v2.preprocess_input(arr)

    # Add batch dimension
    arr = np.expand_dims(arr, axis=0)
    return arr


def predict(image_input):
    """
    Run crop disease prediction on an image.

    Returns:
        dict: {
            "crop_name": str,
            "disease_name": str,
            "class_name": str,
            "confidence": float,        # 0.0 – 1.0
            "is_healthy": bool,
            "severity": str,
            "top_predictions": [        # top-3
                {"class_name": str, "crop": str, "disease": str, "confidence": float}
            ],
            "disease_info": dict | None
        }
    """
    model, class_names, disease_info = _load_resources()

    # Preprocess
    arr = preprocess_image(image_input)

    # Inference
    preds = model.predict(arr, verbose=0)[0]  # shape: (num_classes,)

    # Top-3 predictions
    top3_indices = np.argsort(preds)[::-1][:3]
    top3 = []
    for idx in top3_indices:
        cn = class_names[idx]
        parts = cn.split('___')
        crop_part    = parts[0].replace('_', ' ').replace(',', '').strip()
        disease_part = parts[1].replace('_', ' ').strip() if len(parts) > 1 else 'Unknown'
        top3.append({
            "class_name": cn,
            "crop": crop_part,
            "disease": disease_part,
            "confidence": float(preds[idx]),
        })

    # Best prediction
    best = top3[0]
    best_class = best['class_name']

    # Disease info from JSON
    info = disease_info.get(best_class, {})

    return {
        "crop_name":        best['crop'],
        "disease_name":     info.get('disease_name', best['disease']),
        "class_name":       best_class,
        "confidence":       best['confidence'],
        "is_healthy":       info.get('is_healthy', 'healthy' in best_class.lower()),
        "severity":         info.get('severity', 'Moderate'),
        "severity_color":   info.get('severity_color', '#f59e0b'),
        "top_predictions":  top3,
        "disease_info":     info,
    }


def model_loaded():
    """Check if model is loaded without raising an error."""
    try:
        model_path = os.path.join(SCRIPT_DIR, 'agriscan_model.h5')
        return os.path.exists(model_path)
    except Exception:
        return False
