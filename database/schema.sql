-- Wallet Management System Database Schema
-- Drop existing database and create fresh
DROP DATABASE IF EXISTS wallet_db;
CREATE DATABASE wallet_db;
USE wallet_db;

-- Table 1: Users
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    kyc_status ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    user_role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username)
);

-- Table 2: Wallets
CREATE TABLE wallets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    wallet_number VARCHAR(20) UNIQUE NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'PKR',
    wallet_status ENUM('ACTIVE', 'INACTIVE', 'FROZEN', 'BLOCKED') DEFAULT 'ACTIVE',
    wallet_type ENUM('PERSONAL', 'BUSINESS', 'SAVINGS') DEFAULT 'PERSONAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_wallet_number (wallet_number)
);

-- Table 3: Transactions
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_ref VARCHAR(50) UNIQUE NOT NULL,
    wallet_id BIGINT NOT NULL,
    transaction_type ENUM('CREDIT', 'DEBIT', 'TRANSFER_IN', 'TRANSFER_OUT', 'PAYMENT', 'REFUND', 'WITHDRAWAL', 'TOP_UP') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'PKR',
    balance_before DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    transaction_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    description TEXT,
    recipient_wallet_id BIGINT,
    payment_method VARCHAR(50),
    transaction_fee DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_wallet_id) REFERENCES wallets(id) ON DELETE SET NULL,
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_transaction_ref (transaction_ref),
    INDEX idx_created_at (created_at),
    INDEX idx_status (transaction_status)
);

-- Table 4: Payment Methods
CREATE TABLE payment_methods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    method_type ENUM('BANK_ACCOUNT', 'CARD', 'MOBILE_WALLET', 'UPI') NOT NULL,
    provider_name VARCHAR(100),
    account_number VARCHAR(100),
    account_holder_name VARCHAR(100),
    is_default BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Table 5: Transaction Limits
CREATE TABLE transaction_limits (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wallet_id BIGINT NOT NULL UNIQUE,
    daily_limit DECIMAL(15, 2) DEFAULT 50000.00,
    monthly_limit DECIMAL(15, 2) DEFAULT 500000.00,
    per_transaction_limit DECIMAL(15, 2) DEFAULT 25000.00,
    daily_spent DECIMAL(15, 2) DEFAULT 0.00,
    monthly_spent DECIMAL(15, 2) DEFAULT 0.00,
    last_reset_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE
);

-- Table 6: Wallet Notifications
CREATE TABLE wallet_notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wallet_id BIGINT NOT NULL,
    notification_type ENUM('TRANSACTION', 'SECURITY', 'LIMIT', 'PROMOTIONAL', 'SYSTEM') NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_created_at (created_at)
);

-- Table 7: KYC Documents
CREATE TABLE kyc_documents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    document_type ENUM('CNIC', 'PASSPORT', 'DRIVING_LICENSE', 'UTILITY_BILL') NOT NULL,
    document_number VARCHAR(50),
    document_path VARCHAR(255),
    verification_status ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    verified_at TIMESTAMP NULL,
    verified_by BIGINT,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Table 8: Payment Links (for payment collection)
CREATE TABLE payment_links (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wallet_id BIGINT NOT NULL,
    link_code VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    description TEXT,
    expiry_date TIMESTAMP,
    is_used BOOLEAN DEFAULT FALSE,
    payment_status ENUM('PENDING', 'COMPLETED', 'EXPIRED', 'CANCELLED') DEFAULT 'PENDING',
    paid_by_wallet_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    FOREIGN KEY (paid_by_wallet_id) REFERENCES wallets(id) ON DELETE SET NULL,
    INDEX idx_link_code (link_code)
);

-- Insert Sample Data

-- Sample Users (password for all users is: password123)
INSERT INTO users (username, email, password, full_name, phone_number, kyc_status, user_role) VALUES
('asad_khan', 'asad@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Asad Khan', '+92-300-1234567', 'VERIFIED', 'SUPERUSER'),
('ali_ahmed', 'ali@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ali Ahmed', '+92-301-2345678', 'VERIFIED', 'USER'),
('sara_malik', 'sara@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sara Malik', '+92-302-3456789', 'PENDING', 'USER');

-- Sample Wallets (password for all users is: password123)
INSERT INTO wallets (user_id, wallet_number, balance, wallet_status, wallet_type) VALUES
(1, 'WLT-001-2024-0001', 50000.00, 'ACTIVE', 'PERSONAL'),
(2, 'WLT-001-2024-0002', 25000.00, 'ACTIVE', 'PERSONAL'),
(3, 'WLT-001-2024-0003', 1000.00, 'ACTIVE', 'PERSONAL');

-- Sample Transaction Limits
INSERT INTO transaction_limits (wallet_id, daily_limit, monthly_limit, per_transaction_limit, last_reset_date) VALUES
(1, 50000.00, 500000.00, 25000.00, CURDATE()),
(2, 50000.00, 500000.00, 25000.00, CURDATE()),
(3, 50000.00, 500000.00, 25000.00, CURDATE());

-- Sample Transactions
INSERT INTO transactions (transaction_ref, wallet_id, transaction_type, amount, balance_before, balance_after, transaction_status, description, completed_at) VALUES
('TXN-2024-11-15-0001', 1, 'TOP_UP', 50000.00, 0.00, 50000.00, 'COMPLETED', 'Initial wallet top-up', NOW()),
('TXN-2024-11-15-0002', 2, 'TOP_UP', 25000.00, 0.00, 25000.00, 'COMPLETED', 'Initial wallet top-up', NOW()),
('TXN-2024-11-15-0003', 3, 'TOP_UP', 1000.00, 0.00, 1000.00, 'COMPLETED', 'Initial wallet top-up', NOW());

-- Sample Payment Methods
INSERT INTO payment_methods (user_id, method_type, provider_name, account_number, account_holder_name, is_default, is_verified) VALUES
(1, 'BANK_ACCOUNT', 'HBL', 'PK36HABB0010117894651', 'Asad Khan', TRUE, TRUE),
(2, 'BANK_ACCOUNT', 'MCB', 'PK47MUCB0010123456789', 'Ali Ahmed', TRUE, TRUE);

-- Sample Notifications
INSERT INTO wallet_notifications (wallet_id, notification_type, title, message, priority) VALUES
(1, 'TRANSACTION', 'Wallet Activated', 'Your wallet has been successfully activated and is ready to use.', 'HIGH'),
(2, 'TRANSACTION', 'Wallet Activated', 'Your wallet has been successfully activated and is ready to use.', 'HIGH'),
(3, 'SECURITY', 'KYC Verification Pending', 'Please complete your KYC verification to unlock full wallet features.', 'HIGH');

-- Views for Quick Analytics

-- View: Daily Transaction Summary
CREATE VIEW daily_transaction_summary AS
SELECT 
    DATE(created_at) as transaction_date,
    COUNT(*) as total_transactions,
    SUM(CASE WHEN transaction_type IN ('CREDIT', 'TOP_UP', 'TRANSFER_IN') THEN amount ELSE 0 END) as total_credits,
    SUM(CASE WHEN transaction_type IN ('DEBIT', 'PAYMENT', 'WITHDRAWAL', 'TRANSFER_OUT') THEN amount ELSE 0 END) as total_debits,
    SUM(amount) as net_amount
FROM transactions
WHERE transaction_status = 'COMPLETED'
GROUP BY DATE(created_at)
ORDER BY transaction_date DESC;

-- View: Wallet Summary
CREATE VIEW wallet_summary AS
SELECT 
    w.id,
    w.wallet_number,
    u.full_name,
    u.email,
    w.balance,
    w.wallet_status,
    COUNT(t.id) as total_transactions,
    COALESCE(SUM(CASE WHEN t.transaction_status = 'COMPLETED' THEN t.amount ELSE 0 END), 0) as total_transaction_amount
FROM wallets w
JOIN users u ON w.user_id = u.id
LEFT JOIN transactions t ON w.id = t.wallet_id
GROUP BY w.id, w.wallet_number, u.full_name, u.email, w.balance, w.wallet_status;

-- Indexes for Performance
CREATE INDEX idx_transactions_date_status ON transactions(created_at, transaction_status);
CREATE INDEX idx_wallets_status ON wallets(wallet_status);
CREATE INDEX idx_users_kyc ON users(kyc_status);
