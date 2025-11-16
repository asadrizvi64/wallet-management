-- Script to verify database state after migration
-- Run this to check if demo accounts exist

USE wallet_db;

-- Check if tables exist
SELECT 'Tables in database:' as check_type;
SHOW TABLES;

-- Check users table structure
SELECT 'Users table structure:' as check_type;
DESCRIBE users;

-- Check all users in the database
SELECT 'All users in database:' as check_type;
SELECT
    id,
    username,
    email,
    full_name,
    user_role,
    is_active,
    kyc_status,
    created_at
FROM users
ORDER BY created_at DESC;

-- Check wallets
SELECT 'All wallets in database:' as check_type;
SELECT
    id,
    wallet_number,
    user_id,
    balance,
    wallet_status,
    created_at
FROM wallets
ORDER BY created_at DESC;

-- Check flyway schema history
SELECT 'Flyway migration history:' as check_type;
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
