"""Admin Routes"""
from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from sqlalchemy import func
from datetime import datetime, timedelta

from models import db
from models.user import User
from models.scan import Scan
from models.disease import Disease

admin_bp = Blueprint('admin', __name__)

def admin_required(fn):
    from functools import wraps
    @wraps(fn)
    def wrapper(*args, **kwargs):
        current_user_id = int(get_jwt_identity())
        user = User.query.get(current_user_id)
        if not user or user.role != 'admin':
            return jsonify({'message': 'Admin access required'}), 403
        return fn(*args, **kwargs)
    return wrapper

@admin_bp.route('/dashboard', methods=['GET'])
@jwt_required()
@admin_required
def get_dashboard():
    total_users = User.query.filter_by(role='farmer').count()
    total_scans = Scan.query.count()
    
    # Disease Analytics
    disease_distribution = db.session.query(
        Scan.disease_name, 
        func.count(Scan.id).label('count')
    ).group_by(Scan.disease_name).all()
    
    # Monthly Reports (last 6 months)
    # Simple approach for SQLite/MySQL compatibility
    six_months_ago = datetime.utcnow() - timedelta(days=180)
    recent_scans = Scan.query.filter(Scan.scan_date >= six_months_ago).all()
    
    # Process monthly data in Python
    monthly_counts = {}
    for scan in recent_scans:
        month_key = scan.scan_date.strftime('%Y-%m')
        monthly_counts[month_key] = monthly_counts.get(month_key, 0) + 1
        
    monthly_data = [{'month': k, 'count': v} for k, v in sorted(monthly_counts.items())]
    
    return jsonify({
        'stats': {
            'total_users': total_users,
            'total_scans': total_scans
        },
        'disease_distribution': [{'name': d.disease_name, 'count': d.count} for d in disease_distribution],
        'monthly_reports': monthly_data
    }), 200

@admin_bp.route('/users', methods=['GET'])
@jwt_required()
@admin_required
def get_users():
    users = User.query.filter_by(role='farmer').all()
    return jsonify([user.to_dict() for user in users]), 200

@admin_bp.route('/users/<int:user_id>', methods=['DELETE'])
@jwt_required()
@admin_required
def delete_user(user_id):
    user = User.query.get(user_id)
    if not user:
        return jsonify({'message': 'User not found'}), 404
        
    db.session.delete(user)
    db.session.commit()
    return jsonify({'message': 'User deleted successfully'}), 200
