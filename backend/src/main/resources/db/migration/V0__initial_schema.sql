-- Initial schema migration for Wallet Management System
-- Creates all tables with proper structure including SUPERUSER role and CNIC

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    cnic_number VARCHAR(20),
    kyc_status ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    user_role ENUM('USER', 'ADMIN', 'SUPERUSER') NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_user_role (user_role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create wallets table
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    wallet_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'PKR',
    wallet_status ENUM('ACTIVE', 'INACTIVE', 'FROZEN', 'BLOCKED') DEFAULT 'ACTIVE',
    wallet_type ENUM('PERSONAL', 'BUSINESS', 'SAVINGS') DEFAULT 'PERSONAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_wallet_number (wallet_number),
    INDEX idx_wallet_status (wallet_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_ref VARCHAR(50) NOT NULL UNIQUE,
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
    INDEX idx_transaction_ref (transaction_ref),
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_transaction_status (transaction_status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create payment_methods table
CREATE TABLE IF NOT EXISTS payment_methods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    payment_type ENUM('CREDIT_CARD', 'DEBIT_CARD', 'BANK_ACCOUNT', 'UPI', 'MOBILE_WALLET') NOT NULL,
    provider_name VARCHAR(100),
    account_number VARCHAR(50),
    card_last_four VARCHAR(4),
    card_brand VARCHAR(50),
    expiry_month INT,
    expiry_year INT,
    is_default BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE,
    status ENUM('ACTIVE', 'INACTIVE', 'EXPIRED', 'BLOCKED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_payment_type (payment_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create transaction_limits table
CREATE TABLE IF NOT EXISTS transaction_limits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL UNIQUE,
    daily_limit DECIMAL(15, 2) NOT NULL,
    monthly_limit DECIMAL(15, 2) NOT NULL,
    per_transaction_limit DECIMAL(15, 2) NOT NULL,
    daily_spent DECIMAL(15, 2) DEFAULT 0.00,
    monthly_spent DECIMAL(15, 2) DEFAULT 0.00,
    last_reset_date DATE,
    monthly_reset_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    INDEX idx_wallet_id (wallet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create payment_links table
CREATE TABLE IF NOT EXISTS payment_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    link_code VARCHAR(50) NOT NULL UNIQUE,
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
    INDEX idx_link_code (link_code),
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create wallet_notifications table
CREATE TABLE IF NOT EXISTS wallet_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    notification_type ENUM('TRANSACTION', 'SYSTEM', 'ALERT', 'PROMOTION', 'KYC', 'SECURITY') NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    action_url VARCHAR(500),
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_notification_type (notification_type),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
