"""AgriScan AI – Flask Application Factory"""
import os
from flask import Flask, send_from_directory
from flask_cors import CORS
from flask_mail import Mail

from config import Config
from models import db, jwt

mail = Mail()


def create_app(config_class=Config):
    static_folder = os.path.join(os.path.dirname(__file__), '..', 'frontend', 'dist')
    app = Flask(__name__, static_folder=static_folder, static_url_path='')
    app.config.from_object(config_class)

    # ── Extensions ────────────────────────────────────────────────────────
    db.init_app(app)
    jwt.init_app(app)
    mail.init_app(app)

    CORS(app,
         resources={r"/api/*": {"origins": app.config['FRONTEND_URL']}},
         supports_credentials=True,
         allow_headers=["Content-Type", "Authorization"],
         methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"])

    # ── Upload folder ─────────────────────────────────────────────────────
    os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)

    # ── Blueprints ────────────────────────────────────────────────────────
    from routes.auth      import auth_bp
    from routes.predict   import predict_bp
    from routes.history   import history_bp
    from routes.dashboard import dashboard_bp
    from routes.admin     import admin_bp
    from routes.profile   import profile_bp

    app.register_blueprint(auth_bp,      url_prefix='/api/auth')
    app.register_blueprint(predict_bp,   url_prefix='/api')
    app.register_blueprint(history_bp,   url_prefix='/api')
    app.register_blueprint(dashboard_bp, url_prefix='/api')
    app.register_blueprint(admin_bp,     url_prefix='/api/admin')
    app.register_blueprint(profile_bp,   url_prefix='/api')

    # ── Static uploads ────────────────────────────────────────────────────
    @app.route('/uploads/<path:filename>')
    def serve_upload(filename):
        return send_from_directory(app.config['UPLOAD_FOLDER'], filename)

    # ── Health check ──────────────────────────────────────────────────────
    @app.route('/api/health')
    def health():
        from utils.ml_engine import model_ready
        return {'status': 'ok', 'model_ready': model_ready()}

    # ── Serve Single Page App ───────────────────────────────────────────────
    @app.route('/', defaults={'path': ''})
    @app.route('/<path:path>')
    def serve_frontend(path):
        if not app.static_folder or not os.path.isdir(app.static_folder):
            return {'error': 'Frontend not built. Run: cd ../frontend && npm run build'}, 404
        
        file_path = os.path.join(app.static_folder, path)
        if os.path.isfile(file_path):
            return send_from_directory(app.static_folder, path)
        
        index_path = os.path.join(app.static_folder, 'index.html')
        if os.path.isfile(index_path):
            return send_from_directory(app.static_folder, 'index.html')
        
        return {'error': 'Frontend not found'}, 404

    # ── Create tables ─────────────────────────────────────────────────────
    with app.app_context():
        db.create_all()

    return app
