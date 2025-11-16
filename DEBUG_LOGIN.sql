-- ============================================================================
-- DEBUG LOGIN ISSUES - Run this to diagnose why login still fails
-- ============================================================================

USE wallet_db;

SELECT '========== USER STATUS CHECK ==========' AS step;
SELECT
    id,
    username,
    email,
    is_active,
    user_role,
    kyc_status,
    LEFT(password, 20) AS password_sample,
    CASE
        WHEN password LIKE '$2a$%' THEN '✓ BCrypt encrypted (OK)'
        WHEN password LIKE '$2b$%' THEN '✓ BCrypt encrypted (OK)'
        WHEN password LIKE '$2y$%' THEN '✓ BCrypt encrypted (OK)'
        WHEN LENGTH(password) < 20 THEN '✗ NOT encrypted - PLAIN TEXT!'
        ELSE '? Unknown encryption'
    END AS password_status,
    LENGTH(password) AS password_length,
    created_at
FROM users
ORDER BY id;

SELECT '========== WALLET STATUS CHECK ==========' AS step;
SELECT
    u.id AS user_id,
    u.email,
    w.id AS wallet_id,
    w.wallet_number,
    w.wallet_status,
    CASE
        WHEN w.id IS NULL THEN '✗ NO WALLET - Login may fail!'
        WHEN w.wallet_status != 'ACTIVE' THEN '⚠ Wallet not active'
        ELSE '✓ Wallet OK'
    END AS wallet_check
FROM users u
LEFT JOIN wallets w ON u.id = w.user_id
ORDER BY u.id;

SELECT '========== TEST CREDENTIALS ==========' AS step;
SELECT
    'Use these credentials to test login:' AS instruction,
    '' AS blank;

SELECT
    email AS email_to_use,
    'The password you registered with' AS password_to_use,
    user_role,
    is_active
FROM users
WHERE is_active = 1
ORDER BY
    CASE user_role
        WHEN 'SUPERUSER' THEN 1
        WHEN 'ADMIN' THEN 2
        ELSE 3
    END,
    id
LIMIT 5;

SELECT '========== SAMPLE USERS (from schema.sql) ==========' AS step;
SELECT
    'These are ONLY if you initialized with schema.sql:' AS note,
    '' AS blank;

SELECT
    'asad@example.com' AS email,
    'password123' AS password,
    'SUPERUSER' AS role
UNION ALL
SELECT
    'ali@example.com' AS email,
    'password123' AS password,
    'USER' AS role
UNION ALL
SELECT
    'sara@example.com' AS email,
    'password123' AS password,
    'USER' AS role;

SELECT '========== POTENTIAL ISSUES ==========' AS step;
SELECT
    COUNT(*) AS total_users,
    SUM(CASE WHEN is_active = 1 THEN 1 ELSE 0 END) AS active_users,
    SUM(CASE WHEN password LIKE '$2%' THEN 1 ELSE 0 END) AS properly_encrypted,
    SUM(CASE WHEN password NOT LIKE '$2%' THEN 1 ELSE 0 END) AS NOT_encrypted_PROBLEM,
    SUM(CASE WHEN w.id IS NULL THEN 1 ELSE 0 END) AS users_without_wallet
FROM users u
LEFT JOIN wallets w ON u.id = w.user_id;

SELECT '========== NEXT STEPS ==========' AS step;
SELECT
    CASE
        WHEN (SELECT COUNT(*) FROM users WHERE password NOT LIKE '$2%') > 0 THEN
            '⚠ PROBLEM: Some passwords are not encrypted! You need to re-register these users.'
        WHEN (SELECT COUNT(*) FROM users u LEFT JOIN wallets w ON u.id = w.user_id WHERE w.id IS NULL) > 0 THEN
            '⚠ PROBLEM: Some users have no wallet! Run wallet fix script.'
        WHEN (SELECT COUNT(*) FROM users WHERE is_active = 0) > 0 THEN
            '⚠ PROBLEM: Some users are inactive! Run COMPLETE_LOGIN_FIX.sql again.'
        ELSE
            '✓ Database looks good! Check backend application and logs.'
    END AS diagnosis;
