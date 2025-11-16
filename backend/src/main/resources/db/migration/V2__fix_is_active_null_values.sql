-- Migration to fix NULL is_active values
-- This ensures all users have a valid is_active status

-- Update all NULL is_active values to TRUE (default active state)
UPDATE users SET is_active = TRUE WHERE is_active IS NULL;

-- Modify the column to NOT NULL with DEFAULT TRUE to prevent future NULL values
ALTER TABLE users MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;
