#!/bin/bash

# ============================================================================
# LOGIN FIX SCRIPT - Run this to fix login issues immediately
# ============================================================================

echo "========================================="
echo "WALLET MANAGEMENT - LOGIN FIX SCRIPT"
echo "========================================="
echo ""
echo "This script will fix the login authentication issue"
echo ""

# Check if MySQL/MariaDB is running
if ! command -v mysql &> /dev/null && ! command -v mariadb &> /dev/null; then
    echo "❌ ERROR: MySQL/MariaDB client not found!"
    echo "Please install MySQL client or run the SQL script manually."
    exit 1
fi

# Determine which command to use
if command -v mariadb &> /dev/null; then
    DB_CMD="mariadb"
else
    DB_CMD="mysql"
fi

echo "🔧 Applying database fix..."
echo ""

# Run the fix script
$DB_CMD -h localhost -u root -proot < COMPLETE_LOGIN_FIX.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Database fix applied successfully!"
    echo ""
    echo "========================================="
    echo "NEXT STEPS:"
    echo "========================================="
    echo "1. Restart your backend application"
    echo "2. Try logging in with any registered email and password"
    echo "3. If issues persist, check the backend logs"
    echo ""
    echo "Sample test login (from schema.sql):"
    echo "  Email: asad@example.com"
    echo "  Password: password123"
    echo ""
else
    echo ""
    echo "❌ ERROR: Failed to apply fix"
    echo ""
    echo "Manual fix steps:"
    echo "1. Connect to your database: $DB_CMD -h localhost -u root -p"
    echo "2. Run: source COMPLETE_LOGIN_FIX.sql"
    echo "3. Check the output for any errors"
    echo ""
fi
