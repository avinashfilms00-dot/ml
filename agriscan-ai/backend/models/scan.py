"""Scan model"""
from datetime import datetime
from models import db


class Scan(db.Model):
    __tablename__ = 'scans'

    id              = db.Column(db.Integer, primary_key=True)
    user_id         = db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'), nullable=False)
    image_path      = db.Column(db.String(500), nullable=False)
    image_filename  = db.Column(db.String(255))
    crop_name       = db.Column(db.String(100))
    disease_name    = db.Column(db.String(200))
    class_name      = db.Column(db.String(200))
    confidence      = db.Column(db.Numeric(5, 4))
    is_healthy      = db.Column(db.Boolean, default=False)
    severity        = db.Column(db.Enum('None', 'Low', 'Moderate', 'High', 'Critical'))
    top_predictions = db.Column(db.JSON)
    disease_id      = db.Column(db.Integer, db.ForeignKey('diseases.id', ondelete='SET NULL'), nullable=True)
    notes           = db.Column(db.Text)
    scan_date       = db.Column(db.DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            'id':              self.id,
            'user_id':         self.user_id,
            'image_path':      self.image_path,
            'image_filename':  self.image_filename,
            'crop_name':       self.crop_name,
            'disease_name':    self.disease_name,
            'class_name':      self.class_name,
            'confidence':      float(self.confidence) if self.confidence else None,
            'is_healthy':      self.is_healthy,
            'severity':        self.severity,
            'top_predictions': self.top_predictions,
            'disease_id':      self.disease_id,
            'notes':           self.notes,
            'scan_date':       self.scan_date.isoformat() if self.scan_date else None,
        }

    def __repr__(self):
        return f'<Scan {self.id} – {self.disease_name}>'
