# Wallet Management System

**Enterprise Information System Module - Complete Implementation**

## 📋 Project Overview

This is a complete, production-ready Wallet Management System built as an Enterprise Information System module. The system provides comprehensive wallet operations, transaction management, payment processing, and analytics capabilities.

### ✨ Key Features

- **21+ API Endpoints** for complete wallet management
- **User Authentication** with JWT (simplified for demo)
- **Multiple Wallet Types** (Personal, Business, Savings)
- **Transaction Management** (Add Money, Withdraw, Transfer, Payment)
- **Payment Links** for collecting payments
- **Transaction Limits** with daily/monthly controls
- **Real-time Notifications**
- **Analytics & Reports**
- **RESTful API** with Swagger documentation
- **React Dashboard** for user interaction
- **MySQL Database** with comprehensive schema

---

## 🏗️ System Architecture

### Technology Stack

**Backend:**
- Java 17
- Spring Boot 3.1.5
- Spring Data JPA
- Spring Security
- MySQL 8.0
- Swagger/OpenAPI 3.0
- Maven

**Frontend:**
- React 18
- Material-UI (MUI)
- Axios
- React Router

**Tools:**
- Postman (API Testing)
- MySQL Workbench
- VS Code / IntelliJ IDEA

---

## 📁 Project Structure

```
wallet-management/
├── backend/
│   ├── src/main/java/com/enterprise/wallet/
│   │   ├── controller/          # REST API Controllers
│   │   ├── service/             # Business Logic
│   │   ├── repository/          # Data Access Layer
│   │   ├── model/               # Entity Models
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── config/              # Configuration
│   │   ├── exception/           # Exception Handling
│   │   └── WalletManagementApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── schema.sql
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── App.js               # Main Application
│   │   ├── index.js
│   │   └── index.css
│   ├── public/
│   └── package.json
├── postman/
│   └── Wallet_Management_APIs.postman_collection.json
└── README.md
```

---

## 🚀 Setup Instructions

### Prerequisites

- Java 17 or higher
- Node.js 16+ and npm
- MySQL 8.0
- Maven 3.6+
- Postman (for API testing)

### Step 1: Database Setup

```bash
# Login to MySQL
mysql -u root -p

# The application will auto-create the database
# Or manually run the schema.sql file
source /path/to/backend/src/main/resources/schema.sql
```

### Step 2: Backend Setup

```bash
# Navigate to backend directory
cd wallet-management/backend

# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run

# Application will start on http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Step 3: Frontend Setup

```bash
# Navigate to frontend directory
cd wallet-management/frontend

# Install dependencies
npm install

# Start the development server
npm start

# Application will start on http://localhost:3000
```

---

## 🔌 API Endpoints

### Authentication APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |

### Wallet Management APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/wallets/create` | Create new wallet |
| GET | `/api/wallets/{id}` | Get wallet details |
| GET | `/api/wallets/{id}/balance` | Get wallet balance |
| PUT | `/api/wallets/{id}/status` | Update wallet status |
| GET | `/api/wallets/user/{userId}` | Get all user wallets |

### Transaction APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions/add-money` | Add money to wallet |
| POST | `/api/transactions/withdraw` | Withdraw money |
| POST | `/api/transactions/transfer` | Transfer between wallets |
| POST | `/api/transactions/payment` | Process payment |
| GET | `/api/transactions/{ref}` | Get transaction details |
| GET | `/api/transactions/wallet/{id}/history` | Get transaction history |
| PUT | `/api/transactions/{ref}/cancel` | Cancel transaction |
| POST | `/api/transactions/refund` | Refund transaction |

### Payment Link APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payment-links/generate` | Generate payment link |
| POST | `/api/payment-links/verify` | Verify and process payment |
| GET | `/api/payment-links/gateway/status` | Check gateway status |

### Transaction Limit APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/limits/wallet/{id}` | Get transaction limits |
| PUT | `/api/limits/wallet/{id}` | Set transaction limits |

### Notification APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notifications/wallet/{id}` | Get wallet notifications |
| PUT | `/api/notifications/{id}/read` | Mark as read |

### Analytics APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/analytics/statement` | Generate wallet statement |
| GET | `/api/analytics/spending/{id}` | Get spending analytics |

---

## 📊 Database Schema

### Tables

1. **users** - User accounts and authentication
2. **wallets** - Wallet information
3. **transactions** - All transaction records
4. **payment_methods** - User payment methods
5. **transaction_limits** - Wallet spending limits
6. **wallet_notifications** - Notification system
7. **kyc_documents** - KYC verification (future use)
8. **payment_links** - Payment link generation

---

## 🧪 Testing with Postman

1. Import the Postman collection from `postman/Wallet_Management_APIs.postman_collection.json`
2. Test APIs in this order:
   - Register a new user
   - Login
   - Create wallet
   - Add money
   - Perform transactions
   - Check balance and history

### Sample Test Scenarios

**Scenario 1: User Registration & Wallet Creation**
```
1. POST /api/auth/register
2. POST /api/wallets/create?userId=1
3. GET /api/wallets/user/1
```

**Scenario 2: Complete Transaction Flow**
```
1. POST /api/transactions/add-money (Add PKR 10,000)
2. GET /api/wallets/1/balance (Verify balance)
3. POST /api/transactions/transfer (Transfer PKR 2,000)
4. GET /api/transactions/wallet/1/history (Check history)
```

**Scenario 3: Payment Link Generation**
```
1. POST /api/payment-links/generate
2. POST /api/payment-links/verify
3. GET /api/transactions/wallet/1/history
```

---

## 💻 Frontend Usage

### Login Credentials (Default Users)

After running schema.sql, use these credentials:

```
Username: asad_khan
Password: password123
Email: asad@example.com

Username: ali_ahmed  
Password: password123
Email: ali@example.com
```

### Dashboard Features

1. **Wallet Overview**
   - View all wallets
   - Check balance
   - Create new wallet

2. **Transactions**
   - Add money
   - Withdraw money
   - Transfer to other wallets
   - View transaction history

3. **Notifications**
   - View wallet notifications
   - Transaction alerts
   - System messages

---

## 🔒 Security Features

- Password encryption using BCrypt
- Input validation on all endpoints
- Transaction limits enforcement
- Wallet status controls (Active/Frozen/Blocked)
- Comprehensive error handling

---

## 📈 Key Business Logic

### Transaction Limits

- **Daily Limit**: PKR 50,000 (default)
- **Monthly Limit**: PKR 500,000 (default)
- **Per Transaction**: PKR 25,000 (default)
- Limits are configurable per wallet

### Wallet Status Management

- **ACTIVE**: Normal operations allowed
- **INACTIVE**: Wallet disabled temporarily
- **FROZEN**: Emergency freeze
- **BLOCKED**: Permanently blocked

### Transaction Types

- **TOP_UP**: Add money from external source
- **WITHDRAWAL**: Withdraw to bank account
- **TRANSFER_IN/OUT**: Wallet-to-wallet transfer
- **PAYMENT**: Make payment to merchant
- **REFUND**: Refund processed transaction

---

## 📝 API Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2024-11-15T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "error": "Error type",
  "timestamp": "2024-11-15T10:30:00",
  "path": "/api/endpoint"
}
```

---

## 📚 Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Postman Collection**: Available in `/postman` directory

---

## 🎯 Assignment Deliverables Checklist

✅ **APIs Implementation**
- 21+ functional API endpoints
- RESTful design principles
- Swagger documentation

✅ **Database**
- Complete ERD with 8 tables
- Sample data included
- Optimized indexes

✅ **Frontend**
- React dashboard
- Material-UI components
- Complete CRUD operations

✅ **Testing**
- Postman collection
- Sample test cases
- API response screenshots

✅ **Documentation**
- Comprehensive README
- API documentation
- Setup instructions

✅ **Code Quality**
- Exception handling
- Input validation
- Clean code structure

---

## 🎓 Academic Context

**Course**: Enterprise Information Systems  
**Module**: Wallet Management System  
**Implementation**: Complete end-to-end system

### System Functions (21 Functions Implemented)

1. Create Wallet
2. Get Wallet Details
3. Get Wallet Balance
4. Update Wallet Status
5. Get Wallet History
6. Add Money
7. Withdraw Money
8. Transfer Money
9. Get Transaction Details
10. Get Transaction History
11. Cancel Transaction
12. Refund Transaction
13. Process Payment
14. Generate Payment Link
15. Verify Payment
16. Get Payment Gateway Status
17. Generate Wallet Statement
18. Get Spending Analytics
19. Export Transactions
20. Set Transaction Limit
21. Get Wallet Notifications

---

## 🐛 Troubleshooting

### Common Issues

**1. Database Connection Error**
```
Solution: Check MySQL is running and credentials in application.properties
```

**2. Port Already in Use**
```
Backend (8080): Change server.port in application.properties
Frontend (3000): Use PORT=3001 npm start
```

**3. CORS Error**
```
Solution: Verify CORS configuration in SecurityConfig.java
```

---

## 🚀 Future Enhancements

- Multi-currency support
- Advanced fraud detection
- Mobile app integration
- Blockchain integration
- AI-powered analytics
- Microservices architecture

---

## 👨‍💻 Developer

**Name**: Asad Bhai  
**Project**: Wallet Management System  
**Technology**: Spring Boot + React  
**Date**: November 2024

---

## 📄 License

This project is created for educational purposes as part of an Enterprise Information Systems course assignment.

---

## 📞 Support

For any questions or issues:
- Check Swagger documentation
- Review Postman collection
- Verify database schema
- Check application logs

---

**Note**: This is a complete, working implementation ready for demonstration and submission. All APIs are functional and tested.
