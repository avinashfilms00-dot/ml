"""Prediction Route"""
import os
import uuid
from datetime import datetime
from flask import Blueprint, request, jsonify, current_app
from flask_jwt_extended import jwt_required, get_jwt_identity
from werkzeug.utils import secure_filename

from models import db
from models.scan import Scan
from models.disease import Disease
from utils.ml_engine import predict_image

predict_bp = Blueprint('predict', __name__)

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in current_app.config['ALLOWED_EXTENSIONS']

@predict_bp.route('/predict', methods=['POST'])
@jwt_required()
def predict():
    current_user_id = int(get_jwt_identity())

    if 'image' not in request.files:
        return jsonify({'message': 'No image provided'}), 400
        
    file = request.files['image']
    
    if file.filename == '':
        return jsonify({'message': 'No selected file'}), 400
        
    if file and allowed_file(file.filename):
        filename = secure_filename(file.filename)
        # Create unique filename
        ext = filename.rsplit('.', 1)[1].lower()
        unique_filename = f"{uuid.uuid4().hex}_{datetime.utcnow().strftime('%Y%m%d%H%M%S')}.{ext}"
        filepath = os.path.join(current_app.config['UPLOAD_FOLDER'], unique_filename)
        
        file.save(filepath)
        
        try:
            # Run inference
            result = predict_image(filepath)
            
            # Find disease in DB
            disease_record = Disease.query.filter_by(class_name=result['class_name']).first()
            disease_id = disease_record.id if disease_record else None
            
            # Save scan to DB
            scan = Scan(
                user_id=current_user_id,
                image_path=f"/uploads/{unique_filename}",
                image_filename=unique_filename,
                crop_name=result['crop_name'],
                disease_name=result['disease_name'],
                class_name=result['class_name'],
                confidence=result['confidence'],
                is_healthy=result['is_healthy'],
                severity=result['severity'],
                top_predictions=result['top_predictions'],
                disease_id=disease_id
            )
            
            db.session.add(scan)
            db.session.commit()
            
            # Add DB record info to result
            if disease_record:
                result['disease_info'] = disease_record.to_dict()
                
            result['scan_id'] = scan.id
            result['image_url'] = scan.image_path
            
            return jsonify(result), 200
            
        except Exception as e:
            # Cleanup on failure
            if os.path.exists(filepath):
                os.remove(filepath)
            return jsonify({'message': f'Error processing image: {str(e)}'}), 500
            
    return jsonify({'message': 'Invalid file type'}), 400
