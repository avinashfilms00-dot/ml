"""ML Engine Wrapper"""
import sys
import os
import hashlib
import json

# Add ml directory to path
ML_DIR = os.path.join(os.path.dirname(__file__), '..', '..', 'ml')
sys.path.insert(0, os.path.abspath(ML_DIR))

_ml_module = None
_ml_error = None


def _load_ml_module():
    """Import the TensorFlow-backed ML module only when prediction is needed."""
    global _ml_module, _ml_error
    if _ml_module is not None:
        return _ml_module
    if _ml_error is not None:
        raise RuntimeError(f"ML engine unavailable: {_ml_error}") from _ml_error

    try:
        import predict as ml_module
        _ml_module = ml_module
        return _ml_module
    except ImportError as exc:
        _ml_error = exc
        raise RuntimeError(f"ML engine unavailable: {exc}") from exc

def predict_image(image_path):
    """
    Run prediction on an image file.
    Delegates to the actual ML prediction script.
    """
    try:
        return _load_ml_module().predict(image_path)
    except (RuntimeError, FileNotFoundError, ModuleNotFoundError) as exc:
        return _demo_prediction(image_path, exc)

def model_ready():
    """Check if ML model is ready."""
    try:
        return _load_ml_module().model_loaded()
    except RuntimeError:
        return False


def _demo_prediction(image_path, reason):
    """
    Return a stable demo result when TensorFlow or the trained model is absent.
    This keeps the app usable for uploads while making the real model state clear.
    """
    disease_path = os.path.join(ML_DIR, 'disease_info.json')
    with open(disease_path, 'r', encoding='utf-8') as f:
        disease_info = json.load(f)

    preferred = [
        'Tomato___healthy',
        'Tomato___Early_blight',
        'Tomato___Late_blight',
        'Potato___Early_blight',
        'Apple___Apple_scab',
    ]
    class_names = [name for name in preferred if name in disease_info] or list(disease_info.keys())
    digest = hashlib.sha256(open(image_path, 'rb').read()).digest()
    best_class = class_names[digest[0] % len(class_names)]
    info = disease_info.get(best_class, {})
    ranked_classes = [best_class] + [name for name in class_names if name != best_class][:2]

    top_predictions = []
    for idx, class_name in enumerate(ranked_classes):
        class_info = disease_info.get(class_name, {})
        crop, _, fallback_disease = class_name.partition('___')
        top_predictions.append({
            'class_name': class_name,
            'crop': class_info.get('crop', crop.replace('_', ' ')),
            'disease': class_info.get('disease_name', fallback_disease.replace('_', ' ') or 'Unknown'),
            'confidence': [0.82, 0.12, 0.06][idx],
        })

    crop, _, fallback_disease = best_class.partition('___')
    return {
        'crop_name': info.get('crop', crop.replace('_', ' ')),
        'disease_name': info.get('disease_name', fallback_disease.replace('_', ' ') or 'Unknown'),
        'class_name': best_class,
        'confidence': top_predictions[0]['confidence'],
        'is_healthy': info.get('is_healthy', 'healthy' in best_class.lower()),
        'severity': info.get('severity', 'Moderate'),
        'severity_color': info.get('severity_color', '#f59e0b'),
        'top_predictions': top_predictions,
        'disease_info': {
            **info,
            'description': (
                f"{info.get('description', '')} "
                "Demo prediction shown because the trained TensorFlow model is not installed yet."
            ).strip(),
            'model_status': f'Demo mode: {reason}',
        },
    }
