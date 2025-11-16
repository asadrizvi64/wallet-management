-- Fix NULL is_active values in users table
-- This script updates all NULL is_active values to TRUE and modifies the column to NOT NULL

USE wallet_db;

-- Update all NULL is_active values to TRUE
UPDATE users SET is_active = TRUE WHERE is_active IS NULL;

-- Modify the column to NOT NULL with DEFAULT TRUE
ALTER TABLE users MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- Verify the fix
SELECT id, username, email, is_active FROM users ORDER BY id;
