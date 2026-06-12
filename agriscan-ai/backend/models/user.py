"""User model"""
from datetime import datetime
from models import db


class User(db.Model):
    __tablename__ = 'users'

    id             = db.Column(db.Integer, primary_key=True)
    name           = db.Column(db.String(100), nullable=False)
    email          = db.Column(db.String(150), nullable=False, unique=True)
    password       = db.Column(db.String(255), nullable=False)
    role           = db.Column(db.Enum('farmer', 'admin'), nullable=False, default='farmer')
    phone          = db.Column(db.String(20))
    location       = db.Column(db.String(200))
    profile_image  = db.Column(db.String(500))
    language       = db.Column(db.Enum('en', 'hi'), default='en')
    is_active      = db.Column(db.Boolean, default=True)
    reset_token        = db.Column(db.String(255))
    reset_token_expiry = db.Column(db.DateTime)
    last_login     = db.Column(db.DateTime)
    created_at     = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at     = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationship
    scans = db.relationship('Scan', backref='user', lazy='dynamic', cascade='all, delete-orphan')

    def to_dict(self, include_private=False):
        data = {
            'id':            self.id,
            'name':          self.name,
            'email':         self.email,
            'role':          self.role,
            'phone':         self.phone,
            'location':      self.location,
            'profile_image': self.profile_image,
            'language':      self.language,
            'is_active':     self.is_active,
            'created_at':    self.created_at.isoformat() if self.created_at else None,
            'last_login':    self.last_login.isoformat() if self.last_login else None,
            'scan_count':    self.scans.count(),
        }
        return data

    def __repr__(self):
        return f'<User {self.email}>'
