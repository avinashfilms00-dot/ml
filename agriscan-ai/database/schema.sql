-- ============================================================
-- AgriScan AI – MySQL Database Schema
-- Run: mysql -u root -p < schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS agriscan_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agriscan_db;

-- ============================================================
-- USERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('farmer', 'admin') NOT NULL DEFAULT 'farmer',
    phone VARCHAR(20),
    location VARCHAR(200),
    profile_image VARCHAR(500),
    language ENUM('en', 'hi') DEFAULT 'en',
    is_active BOOLEAN DEFAULT TRUE,
    reset_token VARCHAR(255),
    reset_token_expiry DATETIME,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB;

-- ============================================================
-- DISEASES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS diseases (
    id INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(200) NOT NULL UNIQUE,
    crop VARCHAR(100) NOT NULL,
    disease_name VARCHAR(200) NOT NULL,
    is_healthy BOOLEAN DEFAULT FALSE,
    description TEXT,
    symptoms JSON,
    causes JSON,
    prevention JSON,
    treatment JSON,
    fertilizers JSON,
    severity ENUM('None', 'Low', 'Moderate', 'High', 'Critical') DEFAULT 'Moderate',
    severity_color VARCHAR(20) DEFAULT '#ff9800',
    image_url VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_crop (crop),
    INDEX idx_class_name (class_name)
) ENGINE=InnoDB;

-- ============================================================
-- SCANS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS scans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    image_filename VARCHAR(255),
    crop_name VARCHAR(100),
    disease_name VARCHAR(200),
    class_name VARCHAR(200),
    confidence DECIMAL(5,4),
    is_healthy BOOLEAN DEFAULT FALSE,
    severity ENUM('None', 'Low', 'Moderate', 'High', 'Critical'),
    top_predictions JSON,
    disease_id INT,
    scan_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    INDEX idx_user_id (user_id),
    INDEX idx_scan_date (scan_date),
    INDEX idx_disease_name (disease_name),
    INDEX idx_crop_name (crop_name),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (disease_id) REFERENCES diseases(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- CROP TIPS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS crop_tips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    crop VARCHAR(100),
    tip_en TEXT NOT NULL,
    tip_hi TEXT,
    category ENUM('watering', 'fertilizing', 'pest_control', 'harvesting', 'general') DEFAULT 'general',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- NOTIFICATIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('info', 'warning', 'success', 'error') DEFAULT 'info',
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_notifications (user_id, is_read)
) ENGINE=InnoDB;

-- ============================================================
-- DEFAULT ADMIN USER
-- Password: Admin@123 (bcrypt hashed – update via seed script)
-- ============================================================
INSERT IGNORE INTO users (name, email, password, role)
VALUES ('Admin', 'admin@agriscan.ai', '$2b$12$placeholder_run_seed_py', 'admin');
