-- Migration script to fix user_role ENUM and add missing cnic_number column

-- Step 1: Add cnic_number column if it doesn't exist
-- Note: This will fail silently if column already exists due to the way we handle it
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
AND TABLE_NAME = 'users'
AND COLUMN_NAME = 'cnic_number';

SET @query = IF(@col_exists = 0,
    'ALTER TABLE users ADD COLUMN cnic_number VARCHAR(20) NULL AFTER phone_number',
    'SELECT "Column cnic_number already exists" AS message');

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Step 2: Modify user_role ENUM to include SUPERUSER
-- This is idempotent - will update the ENUM definition
ALTER TABLE users
MODIFY COLUMN user_role ENUM('USER', 'ADMIN', 'SUPERUSER') NOT NULL DEFAULT 'USER';
