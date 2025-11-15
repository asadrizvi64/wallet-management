# 🚀 Quick Start Guide - Wallet Management System

## ⚡ 5-Minute Setup

### Step 1: Database (1 minute)
```bash
# Start MySQL
mysql -u root -p

# Run the schema
source /path/to/wallet-management/backend/src/main/resources/schema.sql
```

### Step 2: Backend (2 minutes)
```bash
cd wallet-management/backend
mvn spring-boot:run
```

✅ Backend running on: **http://localhost:8080**  
✅ Swagger UI: **http://localhost:8080/swagger-ui.html**

### Step 3: Frontend (2 minutes)
```bash
cd wallet-management/frontend
npm install
npm start
```

✅ Frontend running on: **http://localhost:3000**

---

## 🎯 First API Test

### Using Postman:

**1. Register User**
```
POST http://localhost:8080/api/auth/register

Body:
{
  "username": "test_user",
  "email": "test@example.com",
  "password": "password123",
  "fullName": "Test User",
  "phoneNumber": "+92-300-1234567"
}
```

**2. Create Wallet**
```
POST http://localhost:8080/api/wallets/create?userId=1

Body:
{
  "walletType": "PERSONAL"
}
```

**3. Add Money**
```
POST http://localhost:8080/api/transactions/add-money

Body:
{
  "walletId": 1,
  "amount": 10000,
  "paymentMethod": "BANK_ACCOUNT",
  "description": "Initial top-up"
}
```

**4. Check Balance**
```
GET http://localhost:8080/api/wallets/1/balance
```

---

## 🖥️ Using the Web Dashboard

1. Open **http://localhost:3000**
2. Click **"Register New Account"**
3. Fill in your details and register
4. After login, click **"Create New Wallet"**
5. Use **"Add Money"** button to add funds
6. Explore other features!

---

## 📱 Default Test Accounts

Use these accounts to test (after running schema.sql):

```
Account 1:
Username: asad_khan
Password: password123
Wallet: WLT-001-2024-0001
Balance: PKR 50,000

Account 2:
Username: ali_ahmed
Password: password123
Wallet: WLT-001-2024-0002
Balance: PKR 25,000
```

---

## 🧪 Quick Test Scenario

### Test Transfer Between Accounts:

1. **Login as User 1** (asad_khan)
2. Click **"Transfer"**
3. Enter recipient wallet: `WLT-001-2024-0002`
4. Amount: `1000`
5. Click **"Transfer"**
6. Logout and login as User 2 to verify

---

## 📊 Check Swagger Documentation

Visit: **http://localhost:8080/swagger-ui.html**

- Try all APIs directly from browser
- See request/response examples
- Test without Postman

---

## ✅ Verify Installation

### Backend Health Check:
```bash
curl http://localhost:8080/api/payment-links/gateway/status
```

Expected response:
```json
{
  "success": true,
  "message": "Gateway status retrieved",
  "data": {
    "status": "ACTIVE",
    "provider": "Mock Payment Gateway",
    "lastChecked": "2024-11-15T...",
    "availableMethodsCount": 4
  }
}
```

### Database Check:
```sql
USE wallet_db;
SHOW TABLES;
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM wallets;
SELECT COUNT(*) FROM transactions;
```

---

## 🎨 Frontend Features

**Available Actions:**
- ✅ Create Multiple Wallets
- ✅ Add Money
- ✅ Withdraw Money
- ✅ Transfer Between Wallets
- ✅ View Transaction History
- ✅ Check Notifications
- ✅ Real-time Balance Updates

---

## 🔧 Troubleshooting

**Port 8080 already in use?**
```properties
# Edit: backend/src/main/resources/application.properties
server.port=8081
```

**MySQL connection failed?**
```properties
# Edit: backend/src/main/resources/application.properties
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

**Frontend not starting?**
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
npm start
```

---

## 📹 Demo Flow for Presentation

1. **Show Swagger UI** - Demonstrate API documentation
2. **Import Postman Collection** - Show API testing
3. **Run a Transfer** - Live transaction demo
4. **Show Dashboard** - Frontend interaction
5. **Display Database** - Show data persistence

---

## 🎯 What to Demonstrate in Assignment

✅ **API Functionality** (Postman Collection)  
✅ **Database Schema** (ERD + Tables)  
✅ **Frontend Interface** (React Dashboard)  
✅ **Business Logic** (Transaction Flow)  
✅ **Error Handling** (Try invalid operations)  
✅ **Documentation** (Swagger + README)

---

## 📚 Quick Reference

**API Base URL:** `http://localhost:8080/api`  
**Swagger UI:** `http://localhost:8080/swagger-ui.html`  
**Frontend:** `http://localhost:3000`  
**Database:** `wallet_db`

---

**You're all set! 🎉**

The system is now ready for demonstration and submission!
