# Database Migrations

This folder contains SQL migration scripts to update the database schema.

## How to Apply Migrations

### Option 1: Using MySQL Command Line

```bash
mysql -u root -p < database/migrations/001_add_superuser_role.sql
```

### Option 2: Using MySQL Workbench or Similar Tool

1. Open MySQL Workbench
2. Connect to your database
3. Open the migration file
4. Execute the script

### Option 3: Quick Fix via Command Line

```bash
mysql -u root -p -e "USE wallet_db; ALTER TABLE users MODIFY COLUMN user_role ENUM('USER', 'ADMIN', 'SUPERUSER') DEFAULT 'USER';"
```

## Migration History

| Migration | Date | Description |
|-----------|------|-------------|
| 001_add_superuser_role.sql | 2025-11-16 | Add SUPERUSER to user_role ENUM |

## Notes

- Always backup your database before applying migrations
- Migrations should be applied in order
- The application uses `spring.jpa.hibernate.ddl-auto=update` which handles most schema updates automatically, but MySQL ENUM modifications require manual migration scripts
