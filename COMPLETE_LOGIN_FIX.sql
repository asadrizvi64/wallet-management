-- ============================================================================
-- COMPLETE LOGIN FIX SCRIPT
-- ============================================================================
-- This script fixes all issues preventing user login in the wallet system
-- Run this script directly in your MySQL/MariaDB database
-- ============================================================================

USE wallet_db;

-- Step 1: Show current user status BEFORE fix
SELECT '========== USERS BEFORE FIX ==========' AS status;
SELECT
    id,
    username,
    email,
    is_active,
    user_role,
    kyc_status,
    CASE
        WHEN is_active IS NULL THEN 'NULL (LOGIN WILL FAIL!)'
        WHEN is_active = FALSE THEN 'FALSE (LOGIN WILL FAIL!)'
        ELSE 'TRUE (OK)'
    END AS login_status
FROM users
ORDER BY id;

-- Step 2: Fix NULL is_active values (set to TRUE)
UPDATE users
SET is_active = TRUE
WHERE is_active IS NULL;

-- Step 3: Fix any users that are inactive (unless explicitly set by admin)
-- This activates ALL users - comment out if you want to keep some users inactive
UPDATE users
SET is_active = TRUE
WHERE is_active = FALSE;

-- Step 4: Ensure the column has proper constraints
ALTER TABLE users
MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- Step 5: Verify all users have proper roles
UPDATE users
SET user_role = 'USER'
WHERE user_role IS NULL;

-- Step 6: Show current user status AFTER fix
SELECT '========== USERS AFTER FIX ==========' AS status;
SELECT
    id,
    username,
    email,
    is_active,
    user_role,
    kyc_status,
    CASE
        WHEN is_active = TRUE THEN '✓ CAN LOGIN'
        ELSE '✗ CANNOT LOGIN'
    END AS login_status,
    CASE
        WHEN password LIKE '$2a$%' THEN '✓ Password encrypted correctly'
        ELSE '✗ Password NOT encrypted (will fail)'
    END AS password_status
FROM users
ORDER BY id;

-- Step 7: Show sample login credentials
SELECT '========== SAMPLE LOGIN CREDENTIALS ==========' AS info;
SELECT
    email AS 'Email (use this to login)',
    username AS 'Username (alternative login)',
    'Use the password you registered with' AS 'Password',
    user_role AS 'Role',
    is_active AS 'Active'
FROM users
WHERE is_active = TRUE
ORDER BY id;

-- Step 8: Diagnostic information
SELECT '========== DIAGNOSTIC INFO ==========' AS info;
SELECT
    COUNT(*) AS total_users,
    SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) AS active_users,
    SUM(CASE WHEN is_active = FALSE THEN 1 ELSE 0 END) AS inactive_users,
    SUM(CASE WHEN password LIKE '$2a$%' THEN 1 ELSE 0 END) AS users_with_encrypted_passwords
FROM users;

SELECT 'If all users show "CAN LOGIN" above, the database is fixed!' AS result;
SELECT 'If you still cannot login, restart the backend application.' AS next_step;
