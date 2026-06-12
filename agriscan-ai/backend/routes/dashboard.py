"""Dashboard Routes"""
from flask import Blueprint, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from sqlalchemy import func

from models import db
from models.scan import Scan

dashboard_bp = Blueprint('dashboard', __name__)

@dashboard_bp.route('/dashboard', methods=['GET'])
@jwt_required()
def get_dashboard():
    current_user_id = int(get_jwt_identity())
    
    # Total scans
    total_scans = Scan.query.filter_by(user_id=current_user_id).count()
    
    # Healthy vs Diseased
    healthy_scans = Scan.query.filter_by(user_id=current_user_id, is_healthy=True).count()
    diseased_scans = total_scans - healthy_scans
    
    # Recent predictions (last 5)
    recent_scans = Scan.query.filter_by(user_id=current_user_id).order_by(Scan.scan_date.desc()).limit(5).all()
    
    # Disease statistics (group by disease name)
    disease_stats = db.session.query(
        Scan.disease_name, 
        func.count(Scan.id).label('count')
    ).filter(
        Scan.user_id == current_user_id,
        Scan.is_healthy == False
    ).group_by(Scan.disease_name).all()
    
    stats_list = [{'name': stat.disease_name, 'count': stat.count} for stat in disease_stats]
    
    return jsonify({
        'stats': {
            'total_scans': total_scans,
            'healthy_crops': healthy_scans,
            'diseased_crops': diseased_scans
        },
        'recent_predictions': [scan.to_dict() for scan in recent_scans],
        'disease_statistics': stats_list
    }), 200
