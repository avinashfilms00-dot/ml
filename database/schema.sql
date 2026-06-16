-- Database Schema for Nirman Ledger (PostgreSQL)

-- Drop tables if they exist (for clean initialization)
DROP TABLE IF EXISTS payroll CASCADE;
DROP TABLE IF EXISTS expenses CASCADE;
DROP TABLE IF EXISTS advances CASCADE;
DROP TABLE IF EXISTS attendance CASCADE;
DROP TABLE IF EXISTS workers CASCADE;
DROP TABLE IF EXISTS sites CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 1. Users Table (Contractors and Owners)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mobile VARCHAR(20) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'ROLE_CONTRACTOR', 'ROLE_OWNER'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Sites Table
CREATE TABLE sites (
    id SERIAL PRIMARY KEY,
    site_name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    owner_mobile VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'COMPLETED'
    contractor_id INT REFERENCES users(id) ON DELETE CASCADE,
    owner_id INT REFERENCES users(id) ON DELETE SET NULL
);

-- 3. Workers Table
CREATE TABLE workers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    mobile VARCHAR(20) NOT NULL,
    skill VARCHAR(50) NOT NULL, -- e.g., 'MASON', 'HELPER', 'PLUMBER', 'ELECTRICIAN'
    daily_wage NUMERIC(10, 2) NOT NULL,
    active BOOLEAN DEFAULT TRUE NOT NULL,
    site_id INT REFERENCES sites(id) ON DELETE CASCADE
);

-- 4. Attendance Table
CREATE TABLE attendance (
    id SERIAL PRIMARY KEY,
    worker_id INT REFERENCES workers(id) ON DELETE CASCADE,
    site_id INT REFERENCES sites(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    status VARCHAR(20) NOT NULL, -- 'PRESENT', 'HALF_DAY', 'ABSENT'
    CONSTRAINT unique_worker_date UNIQUE (worker_id, date)
);

-- 5. Advances Table
CREATE TABLE advances (
    id SERIAL PRIMARY KEY,
    worker_id INT REFERENCES workers(id) ON DELETE CASCADE,
    site_id INT REFERENCES sites(id) ON DELETE CASCADE,
    amount NUMERIC(10, 2) NOT NULL,
    date DATE NOT NULL,
    note TEXT
);

-- 6. Expenses Table
CREATE TABLE expenses (
    id SERIAL PRIMARY KEY,
    site_id INT REFERENCES sites(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL, -- 'CEMENT', 'SAND', 'STEEL', 'TILES', 'ELECTRICAL', 'MISC'
    amount NUMERIC(10, 2) NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    receipt_url TEXT
);

-- 7. Weekly Payroll Table
CREATE TABLE payroll (
    id SERIAL PRIMARY KEY,
    worker_id INT REFERENCES workers(id) ON DELETE CASCADE,
    site_id INT REFERENCES sites(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    full_days INT NOT NULL DEFAULT 0,
    half_days INT NOT NULL DEFAULT 0,
    daily_wage NUMERIC(10, 2) NOT NULL,
    advance_deducted NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    final_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'PAID'
    paid_date DATE,
    CONSTRAINT unique_worker_payroll_period UNIQUE (worker_id, start_date, end_date)
);

-- Indexes for performance optimization
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_sites_contractor ON sites(contractor_id);
CREATE INDEX idx_sites_owner ON sites(owner_id);
CREATE INDEX idx_workers_site ON workers(site_id);
CREATE INDEX idx_attendance_date ON attendance(date);
CREATE INDEX idx_attendance_worker_date ON attendance(worker_id, date);
CREATE INDEX idx_advances_worker ON advances(worker_id);
CREATE INDEX idx_advances_date ON advances(date);
CREATE INDEX idx_expenses_site ON expenses(site_id);
CREATE INDEX idx_expenses_date ON expenses(date);
CREATE INDEX idx_payroll_worker_dates ON payroll(worker_id, start_date, end_date);

-- Insert Sample Seed Data (Password hashes are for BCrypt "$2a$10$tM2xK9L242eQ4hM..." for 'password')
INSERT INTO users (username, password, email, mobile, role) VALUES
('contractor1', '$2a$10$d6x2oJqRj/V6QyP8K1g/k.i7N2M0tS27JUp77M0V1Zepm0/J3Hxe.', 'contractor1@example.com', '9876543210', 'ROLE_CONTRACTOR'),
('owner1', '$2a$10$d6x2oJqRj/V6QyP8K1g/k.i7N2M0tS27JUp77M0V1Zepm0/J3Hxe.', 'owner1@example.com', '9876543211', 'ROLE_OWNER');

INSERT INTO sites (site_name, address, owner_name, owner_mobile, start_date, status, contractor_id, owner_id) VALUES
('Green Valley Residency', 'Sector 15, Gurgaon', 'Mr. Amit Sharma', '9876543211', '2026-05-01', 'ACTIVE', 1, 2);

INSERT INTO workers (name, mobile, skill, daily_wage, active, site_id) VALUES
('Ramesh Kumar', '9999888877', 'MASON', 600.00, true, 1),
('Suresh Singh', '9999888876', 'HELPER', 400.00, true, 1),
('Vijay Yadav', '9999888875', 'PLUMBER', 700.00, true, 1);

-- Insert sample attendance for Green Valley (Ramesh & Suresh present on June 15, 2026)
INSERT INTO attendance (worker_id, site_id, date, status) VALUES
(1, 1, '2026-06-15', 'PRESENT'),
(2, 1, '2026-06-15', 'HALF_DAY'),
(3, 1, '2026-06-15', 'ABSENT');

-- Insert sample advance
INSERT INTO advances (worker_id, site_id, amount, date, note) VALUES
(1, 1, 200.00, '2026-06-15', 'Emergency cash advance');

-- Insert sample expense
INSERT INTO expenses (site_id, category, amount, date, description, receipt_url) VALUES
(1, 'CEMENT', 12500.00, '2026-06-15', '50 bags of OPC Cement', 'https://via.placeholder.com/150');
