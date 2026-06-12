"""Scan History Routes"""
from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity

from models.scan import Scan

history_bp = Blueprint('history', __name__)

@history_bp.route('/history', methods=['GET'])
@jwt_required()
def get_history():
    current_user_id = int(get_jwt_identity())
    
    # Optional filters
    disease = request.args.get('disease')
    healthy = request.args.get('healthy')
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    
    query = Scan.query.filter_by(user_id=current_user_id)
    
    if disease:
        query = query.filter(Scan.disease_name.ilike(f'%{disease}%'))
    if healthy is not None:
        is_healthy = healthy.lower() in ('true', '1', 't')
        query = query.filter_by(is_healthy=is_healthy)
        
    query = query.order_by(Scan.scan_date.desc())
    
    pagination = query.paginate(page=page, per_page=per_page, error_out=False)
    
    scans = [scan.to_dict() for scan in pagination.items]
    
    return jsonify({
        'scans': scans,
        'total': pagination.total,
        'pages': pagination.pages,
        'current_page': page
    }), 200

@history_bp.route('/history/<int:scan_id>', methods=['GET'])
@jwt_required()
def get_scan(scan_id):
    current_user_id = int(get_jwt_identity())
    scan = Scan.query.filter_by(id=scan_id, user_id=current_user_id).first()
    
    if not scan:
        return jsonify({'message': 'Scan not found'}), 404
        
    result = scan.to_dict()
    if scan.disease:
        result['disease_info'] = scan.disease.to_dict()
        
    return jsonify(result), 200

@history_bp.route('/history/<int:scan_id>', methods=['DELETE'])
@jwt_required()
def delete_scan(scan_id):
    current_user_id = int(get_jwt_identity())
    scan = Scan.query.filter_by(id=scan_id, user_id=current_user_id).first()
    
    if not scan:
        return jsonify({'message': 'Scan not found'}), 404
        
    # Optional: Delete file from filesystem
    # import os
    # from flask import current_app
    # file_path = os.path.join(current_app.config['UPLOAD_FOLDER'], scan.image_filename)
    # if os.path.exists(file_path):
    #     os.remove(file_path)
        
    from models import db
    db.session.delete(scan)
    db.session.commit()
    
    return jsonify({'message': 'Scan deleted successfully'}), 200
