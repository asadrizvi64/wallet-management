#!/bin/bash

# ============================================================================
# LOGIN TEST SCRIPT - Test if backend and login are working
# ============================================================================

echo "========================================="
echo "WALLET MANAGEMENT - LOGIN TEST"
echo "========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Check if backend is running
echo "Test 1: Checking if backend is running on port 8080..."
if curl -s http://localhost:8080/api/v1/users/register > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Backend is running${NC}"
else
    echo -e "${RED}✗ Backend is NOT running or not accessible${NC}"
    echo ""
    echo "Start the backend first:"
    echo "  docker-compose up -d"
    echo "  OR"
    echo "  ./dev-start.sh"
    exit 1
fi

echo ""

# Test 2: Try login with sample credentials
echo "Test 2: Testing login with sample credentials..."
echo "Email: asad@example.com"
echo "Password: password123"
echo ""

RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "asad@example.com",
    "password": "password123"
  }')

echo "Response:"
echo "$RESPONSE" | jq . 2>/dev/null || echo "$RESPONSE"
echo ""

if echo "$RESPONSE" | grep -q '"success":true' || echo "$RESPONSE" | grep -q '"token"'; then
    echo -e "${GREEN}✓ Login SUCCESSFUL!${NC}"
    echo ""
    echo "You can now login with:"
    echo "  Email: asad@example.com"
    echo "  Password: password123"
    exit 0
else
    echo -e "${RED}✗ Login FAILED${NC}"
    echo ""
fi

# Test 3: Try registering a new user
echo "Test 3: Testing registration with new user..."
TEST_EMAIL="test_$(date +%s)@example.com"
TEST_USERNAME="testuser_$(date +%s)"

REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "'"$TEST_USERNAME"'",
    "email": "'"$TEST_EMAIL"'",
    "password": "testpass123",
    "fullName": "Test User",
    "phoneNumber": "+92-300-9999999",
    "cnicNumber": "12345-1234567-1"
  }')

echo "Registration Response:"
echo "$REGISTER_RESPONSE" | jq . 2>/dev/null || echo "$REGISTER_RESPONSE"
echo ""

if echo "$REGISTER_RESPONSE" | grep -q '"success":true' || echo "$REGISTER_RESPONSE" | grep -q 'registered'; then
    echo -e "${GREEN}✓ Registration SUCCESSFUL${NC}"
    echo ""

    # Now try to login with the new user
    echo "Test 4: Testing login with newly registered user..."
    sleep 1

    NEW_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/users/login \
      -H "Content-Type: application/json" \
      -d '{
        "email": "'"$TEST_EMAIL"'",
        "password": "testpass123"
      }')

    echo "Login Response:"
    echo "$NEW_LOGIN_RESPONSE" | jq . 2>/dev/null || echo "$NEW_LOGIN_RESPONSE"
    echo ""

    if echo "$NEW_LOGIN_RESPONSE" | grep -q '"success":true' || echo "$NEW_LOGIN_RESPONSE" | grep -q '"token"'; then
        echo -e "${GREEN}✓ NEW USER LOGIN SUCCESSFUL!${NC}"
        echo ""
        echo "Good news! Registration and login are working!"
        echo "The issue is with your EXISTING users."
        echo ""
        echo "SOLUTION: You need to RE-REGISTER all your accounts"
        echo "OR check if passwords in database are properly encrypted."
        echo ""
        echo "Run: mariadb -h localhost -u root -proot < DEBUG_LOGIN.sql"
        exit 0
    else
        echo -e "${RED}✗ NEW USER LOGIN FAILED${NC}"
        echo ""
        echo "Even newly registered users cannot login!"
        echo "This indicates a backend configuration issue."
    fi
else
    echo -e "${RED}✗ Registration FAILED${NC}"
    echo ""
    echo "Cannot register new users - backend may have errors."
fi

echo ""
echo "========================================="
echo "DEBUGGING STEPS:"
echo "========================================="
echo "1. Check backend logs:"
echo "   docker-compose logs -f backend"
echo ""
echo "2. Check database passwords:"
echo "   mariadb -h localhost -u root -proot < DEBUG_LOGIN.sql"
echo ""
echo "3. Restart backend:"
echo "   docker-compose down && docker-compose up -d"
echo ""
echo "4. Check application.properties:"
echo "   cat backend/src/main/resources/application.properties"
echo ""
