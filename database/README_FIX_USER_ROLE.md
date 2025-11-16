# Database Migration Instructions

## Issue
The application fails to start with the error:
```
SQL Error: 1265, SQLState: 01000
Data truncated for column 'user_role' at row 1
```

## Root Cause
The database schema was created with `user_role` as `ENUM('USER', 'ADMIN')`, but the application tries to insert `'SUPERUSER'` which is not in the allowed values. Additionally, the `cnic_number` column is missing from the database.

## Solution

### Option 1: Run the Migration Script (Recommended)

Run the migration script to fix the existing database:

#### On Windows (using MySQL command line):
```powershell
cd C:\Users\HP\Downloads\wallet-management\database
mysql -u root -proot < migration_fix_user_role.sql
```

Or if you have MySQL Workbench, open and execute `migration_fix_user_role.sql`.

#### On Linux/Mac:
```bash
mysql -u root -proot < database/migration_fix_user_role.sql
```

### Option 2: Recreate the Database

If you don't mind losing existing data, you can recreate the database using the updated schema:

```sql
mysql -u root -proot < database/schema.sql
```

**WARNING:** This will delete all existing data!

### Option 3: Manual Fix via MySQL Console

1. Connect to MySQL:
   ```
   mysql -u root -proot
   ```

2. Run these commands:
   ```sql
   USE wallet_db;

   -- Add cnic_number column if it doesn't exist
   ALTER TABLE users
   ADD COLUMN IF NOT EXISTS cnic_number VARCHAR(20) AFTER phone_number;

   -- Modify user_role ENUM to include SUPERUSER
   ALTER TABLE users
   MODIFY COLUMN user_role ENUM('USER', 'ADMIN', 'SUPERUSER') DEFAULT 'USER';

   -- Verify the changes
   DESCRIBE users;
   ```

## After Migration

Once you've applied the migration, restart the application:

```powershell
cd C:\Users\HP\Downloads\wallet-management\backend
mvn spring-boot:run
```

The application should now start successfully.

## Changes Made

1. **Added SUPERUSER role** to the `user_role` ENUM
2. **Added cnic_number column** to the `users` table
3. **Updated schema.sql** for future database recreations

## Demo Accounts

After the fix, the following demo accounts will be created:

- **SUPERADMIN Account**
  - Username: `superadmin`
  - Email: `superadmin@wallet.com`
  - Password: `SuperAdmin@123`
  - Role: `SUPERUSER`

- **ADMIN Account**
  - Username: `admin`
  - Email: `admin@wallet.com`
  - Password: `Admin@123`
  - Role: `ADMIN`

- **DEMO USER Account**
  - Username: `demouser`
  - Email: `demo@wallet.com`
  - Password: `Demo@123`
  - Role: `USER`
