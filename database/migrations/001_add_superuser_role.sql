-- Migration: Add SUPERUSER to user_role ENUM
-- Date: 2025-11-16
-- Description: Updates the user_role ENUM to include SUPERUSER value

USE wallet_db;

-- Alter the users table to update the user_role ENUM
ALTER TABLE users
MODIFY COLUMN user_role ENUM('USER', 'ADMIN', 'SUPERUSER') DEFAULT 'USER';

-- Verify the change
DESCRIBE users;
