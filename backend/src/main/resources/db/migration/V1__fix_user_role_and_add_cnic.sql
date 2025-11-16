-- Migration script to fix user_role ENUM and add missing cnic_number column

-- Step 1: Add cnic_number column if it doesn't exist
-- Using a stored procedure to safely add the column
DELIMITER //
CREATE PROCEDURE add_cnic_column()
BEGIN
    IF NOT EXISTS (
        SELECT * FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'users'
        AND COLUMN_NAME = 'cnic_number'
    ) THEN
        ALTER TABLE users ADD COLUMN cnic_number VARCHAR(20) AFTER phone_number;
    END IF;
END//
DELIMITER ;

CALL add_cnic_column();
DROP PROCEDURE add_cnic_column;

-- Step 2: Modify user_role ENUM to include SUPERUSER
-- MySQL requires us to modify the column with all existing values plus the new one
ALTER TABLE users
MODIFY COLUMN user_role ENUM('USER', 'ADMIN', 'SUPERUSER') DEFAULT 'USER';
