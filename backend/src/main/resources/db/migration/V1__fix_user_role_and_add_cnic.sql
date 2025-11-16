-- Migration script to fix user_role ENUM and add missing cnic_number column

-- Step 1: Add cnic_number column if it doesn't exist
ALTER TABLE users
ADD COLUMN IF NOT EXISTS cnic_number VARCHAR(20) AFTER phone_number;

-- Step 2: Modify user_role ENUM to include SUPERUSER
-- MySQL requires us to modify the column with all existing values plus the new one
ALTER TABLE users
MODIFY COLUMN user_role ENUM('USER', 'ADMIN', 'SUPERUSER') DEFAULT 'USER';
