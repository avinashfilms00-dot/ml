"""AgriScan AI – Configuration"""
import os
from datetime import timedelta
from dotenv import load_dotenv

load_dotenv(os.path.join(os.path.dirname(__file__), '..', '.env'))


class Config:
    # ── Flask ──────────────────────────────────────────────────────────────
    SECRET_KEY = os.getenv('SECRET_KEY', 'agriscan-secret-change-in-prod')
    DEBUG = os.getenv('FLASK_DEBUG', '0') == '1'

    # ── Database ───────────────────────────────────────────────────────────
    DB_HOST = os.getenv('DB_HOST', 'localhost')
    DB_PORT = os.getenv('DB_PORT', '3306')
    DB_NAME = os.getenv('DB_NAME', 'agriscan_db')
    DB_USER = os.getenv('DB_USER', 'root')
    DB_PASS = os.getenv('DB_PASSWORD', '')

    SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(os.path.dirname(__file__), 'app.db')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_ENGINE_OPTIONS = {}

    # ── JWT ────────────────────────────────────────────────────────────────
    JWT_SECRET_KEY = os.getenv('JWT_SECRET_KEY', 'jwt-secret-change-in-prod')
    JWT_ACCESS_TOKEN_EXPIRES  = timedelta(hours=int(os.getenv('JWT_ACCESS_TOKEN_EXPIRES', '3600')) // 3600)
    JWT_REFRESH_TOKEN_EXPIRES = timedelta(days=7)
    JWT_TOKEN_LOCATION = ['headers']
    JWT_HEADER_NAME = 'Authorization'
    JWT_HEADER_TYPE = 'Bearer'

    # ── Uploads ────────────────────────────────────────────────────────────
    UPLOAD_FOLDER = os.path.join(os.path.dirname(__file__), 'uploads')
    MAX_CONTENT_LENGTH = int(os.getenv('MAX_CONTENT_LENGTH', 16 * 1024 * 1024))
    ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'webp', 'bmp'}

    # ── ML ─────────────────────────────────────────────────────────────────
    ML_DIR = os.path.join(os.path.dirname(__file__), '..', 'ml')
    MODEL_PATH       = os.getenv('MODEL_PATH',       os.path.join(ML_DIR, 'agriscan_model.h5'))
    CLASSES_PATH     = os.getenv('CLASSES_PATH',     os.path.join(ML_DIR, 'classes.json'))
    DISEASE_INFO_PATH= os.getenv('DISEASE_INFO_PATH',os.path.join(ML_DIR, 'disease_info.json'))

    # ── Mail ───────────────────────────────────────────────────────────────
    MAIL_SERVER   = os.getenv('MAIL_SERVER',   'smtp.gmail.com')
    MAIL_PORT     = int(os.getenv('MAIL_PORT', '587'))
    MAIL_USE_TLS  = os.getenv('MAIL_USE_TLS', 'True') == 'True'
    MAIL_USERNAME = os.getenv('MAIL_USERNAME', '')
    MAIL_PASSWORD = os.getenv('MAIL_PASSWORD', '')
    MAIL_DEFAULT_SENDER = os.getenv('MAIL_USERNAME', 'noreply@agriscan.ai')

    # ── CORS ───────────────────────────────────────────────────────────────
    FRONTEND_URL = os.getenv('FRONTEND_URL', 'http://localhost:5173')

    # ── Weather ────────────────────────────────────────────────────────────
    WEATHER_API_KEY = os.getenv('WEATHER_API_KEY', '')
