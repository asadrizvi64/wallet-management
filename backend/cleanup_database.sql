-- Cleanup script to reset the database for fresh migration
-- This will drop all tables and the flyway schema history

USE wallet_db;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables if they exist
DROP TABLE IF EXISTS wallet_notifications;
DROP TABLE IF EXISTS payment_links;
DROP TABLE IF EXISTS transaction_limits;
DROP TABLE IF EXISTS payment_methods;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS users;

-- Drop flyway schema history table
DROP TABLE IF EXISTS flyway_schema_history;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
