-- Seed data for H2 (runs after Hibernate ddl-auto=update creates tables)
-- Password: 'password' hashed with BCrypt
-- Uses MERGE INTO for idempotent inserts

MERGE INTO users (id, username, password, email, mobile, role) KEY(username)
VALUES (1, 'contractor1', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36P1LhX7U.a6g1QfpX8eOqy', 'contractor@nirman.com', '9876543210', 'ROLE_CONTRACTOR');

MERGE INTO users (id, username, password, email, mobile, role) KEY(username)
VALUES (2, 'owner1', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36P1LhX7U.a6g1QfpX8eOqy', 'owner@nirman.com', '9876543211', 'ROLE_OWNER');

MERGE INTO sites (id, site_name, address, owner_name, owner_mobile, start_date, status, contractor_id, owner_id) KEY(site_name)
VALUES (1, 'Green Valley Residency', 'Sector 45, Gurugram, Haryana', 'Rajesh Kumar', '9811111111', '2026-01-15', 'ACTIVE', 1, 2);

MERGE INTO sites (id, site_name, address, owner_name, owner_mobile, start_date, status, contractor_id, owner_id) KEY(site_name)
VALUES (2, 'Sunrise Apartments', 'MG Road, Pune, Maharashtra', 'Amit Patel', '9822222222', '2026-03-01', 'ACTIVE', 1, 2);

MERGE INTO workers (id, name, skill, daily_wage, mobile, site_id, active) KEY(mobile)
VALUES (1, 'Ramesh Yadav', 'Mason', 800.00, '9900000001', 1, TRUE);

MERGE INTO workers (id, name, skill, daily_wage, mobile, site_id, active) KEY(mobile)
VALUES (2, 'Suresh Kumar', 'Carpenter', 750.00, '9900000002', 1, TRUE);

MERGE INTO workers (id, name, skill, daily_wage, mobile, site_id, active) KEY(mobile)
VALUES (3, 'Mukesh Singh', 'Electrician', 900.00, '9900000003', 1, TRUE);

MERGE INTO workers (id, name, skill, daily_wage, mobile, site_id, active) KEY(mobile)
VALUES (4, 'Dinesh Verma', 'Plumber', 700.00, '9900000004', 1, TRUE);

MERGE INTO workers (id, name, skill, daily_wage, mobile, site_id, active) KEY(mobile)
VALUES (5, 'Ganesh Sharma', 'Helper', 500.00, '9900000005', 1, TRUE);
