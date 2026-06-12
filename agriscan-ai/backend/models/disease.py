"""Disease and CropTip models"""
from datetime import datetime
from models import db


class Disease(db.Model):
    __tablename__ = 'diseases'

    id             = db.Column(db.Integer, primary_key=True)
    class_name     = db.Column(db.String(200), nullable=False, unique=True)
    crop           = db.Column(db.String(100), nullable=False)
    disease_name   = db.Column(db.String(200), nullable=False)
    is_healthy     = db.Column(db.Boolean, default=False)
    description    = db.Column(db.Text)
    symptoms       = db.Column(db.JSON)
    causes         = db.Column(db.JSON)
    prevention     = db.Column(db.JSON)
    treatment      = db.Column(db.JSON)
    fertilizers    = db.Column(db.JSON)
    severity       = db.Column(db.Enum('None', 'Low', 'Moderate', 'High', 'Critical'), default='Moderate')
    severity_color = db.Column(db.String(20), default='#f59e0b')
    image_url      = db.Column(db.String(500))
    created_at     = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at     = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    scans = db.relationship('Scan', backref='disease', lazy='dynamic')

    def to_dict(self):
        return {
            'id':             self.id,
            'class_name':     self.class_name,
            'crop':           self.crop,
            'disease_name':   self.disease_name,
            'is_healthy':     self.is_healthy,
            'description':    self.description,
            'symptoms':       self.symptoms,
            'causes':         self.causes,
            'prevention':     self.prevention,
            'treatment':      self.treatment,
            'fertilizers':    self.fertilizers,
            'severity':       self.severity,
            'severity_color': self.severity_color,
            'image_url':      self.image_url,
        }

    def __repr__(self):
        return f'<Disease {self.class_name}>'


class CropTip(db.Model):
    __tablename__ = 'crop_tips'

    id         = db.Column(db.Integer, primary_key=True)
    crop       = db.Column(db.String(100))
    tip_en     = db.Column(db.Text, nullable=False)
    tip_hi     = db.Column(db.Text)
    category   = db.Column(db.Enum('watering', 'fertilizing', 'pest_control', 'harvesting', 'general'), default='general')
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            'id':       self.id,
            'crop':     self.crop,
            'tip_en':   self.tip_en,
            'tip_hi':   self.tip_hi,
            'category': self.category,
        }
