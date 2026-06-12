"""JWT Helper functions"""
from flask_jwt_extended import decode_token

def is_token_valid(token):
    try:
        decode_token(token)
        return True
    except Exception:
        return False
