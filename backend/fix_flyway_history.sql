-- Script to fix Flyway schema history
-- This removes the failed migration entry so Flyway can re-run it successfully

-- Delete the failed migration entry from Flyway's history table
DELETE FROM flyway_schema_history
WHERE version = '1'
AND description = 'fix user role and add cnic';

-- Verify the deletion
SELECT * FROM flyway_schema_history;
