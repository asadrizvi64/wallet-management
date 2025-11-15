# Database Schema & ERD Documentation

## 📊 Entity Relationship Diagram

```
┌─────────────────┐         ┌──────────────────┐
│     USERS       │         │    WALLETS       │
├─────────────────┤         ├──────────────────┤
│ PK id           │1────────M│ PK id            │
│    username     │         │ FK user_id       │
│    email        │         │    wallet_number │
│    password     │         │    balance       │
│    full_name    │         │    currency      │
│    phone_number │         │    wallet_status │
│    kyc_status   │         │    wallet_type   │
│    user_role    │         │    created_at    │
│    is_active    │         │    updated_at    │
│    created_at   │         └──────────────────┘
│    updated_at   │                   │
└─────────────────┘                   │1
         │1                           │
         │                            │
         │                            │M
         │                   ┌──────────────────┐
         │                   │  TRANSACTIONS    │
         │                   ├──────────────────┤
         │                   │ PK id            │
         │                   │ FK wallet_id     │
         │                   │    transaction_ref│
         │                   │    type          │
         │                   │    amount        │
         │                   │    balance_before│
         │                   │    balance_after │
         │                   │    status        │
         │                   │    description   │
         │                   │ FK recipient_id  │
         │                   │    payment_method│
         │                   │    fee           │
         │                   │    created_at    │
         │                   │    completed_at  │
         │                   └──────────────────┘
         │                            │
         │                            │1
         │                            │
         │M                           │M
┌─────────────────┐         ┌──────────────────┐
│ PAYMENT_METHODS │         │ TRANSACTION_LIMITS│
├─────────────────┤         ├──────────────────┤
│ PK id           │         │ PK id            │
│ FK user_id      │         │ FK wallet_id (UQ)│
│    method_type  │         │    daily_limit   │
│    provider     │         │    monthly_limit │
│    account_num  │         │    per_txn_limit │
│    holder_name  │         │    daily_spent   │
│    is_default   │         │    monthly_spent │
│    is_verified  │         │    last_reset    │
│    created_at   │         │    created_at    │
└─────────────────┘         │    updated_at    │
                            └──────────────────┘

┌──────────────────┐        ┌──────────────────┐
│ PAYMENT_LINKS    │        │ WALLET_NOTIFICATIONS│
├──────────────────┤        ├──────────────────┤
│ PK id            │        │ PK id            │
│ FK wallet_id     │M───────1│ FK wallet_id     │
│    link_code (UQ)│        │    type          │
│    amount        │        │    title         │
│    description   │        │    message       │
│    expiry_date   │        │    is_read       │
│    is_used       │        │    priority      │
│    payment_status│        │    created_at    │
│ FK paid_by_id    │        └──────────────────┘
│    created_at    │
│    used_at       │
└──────────────────┘

┌──────────────────┐
│  KYC_DOCUMENTS   │
├──────────────────┤
│ PK id            │
│ FK user_id       │
│    document_type │
│    document_num  │
│    document_path │
│    verify_status │
│    verified_at   │
│ FK verified_by   │
│    reject_reason │
│    created_at    │
└──────────────────┘
```

---

## 📋 Table Descriptions

### 1. users
**Purpose:** Store user account information

**Columns:**
- `id` (BIGINT, PK) - Auto-increment primary key
- `username` (VARCHAR(50), UNIQUE) - Unique username
- `email` (VARCHAR(100), UNIQUE) - Unique email address
- `password` (VARCHAR(255)) - BCrypt encrypted password
- `full_name` (VARCHAR(100)) - User's full name
- `phone_number` (VARCHAR(20)) - Contact number
- `kyc_status` (ENUM) - PENDING, VERIFIED, REJECTED
- `user_role` (ENUM) - USER, ADMIN
- `is_active` (BOOLEAN) - Account active status
- `created_at` (TIMESTAMP) - Registration date
- `updated_at` (TIMESTAMP) - Last update date

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (username)
- UNIQUE (email)
- INDEX (email)
- INDEX (username)

**Sample Data:**
```sql
INSERT INTO users VALUES
(1, 'asad_khan', 'asad@example.com', '$2a$10$...', 'Asad Khan', '+92-300-1234567', 'VERIFIED', 'ADMIN', 1, NOW(), NOW()),
(2, 'ali_ahmed', 'ali@example.com', '$2a$10$...', 'Ali Ahmed', '+92-301-2345678', 'VERIFIED', 'USER', 1, NOW(), NOW());
```

---

### 2. wallets
**Purpose:** Store wallet information for each user

**Columns:**
- `id` (BIGINT, PK) - Auto-increment primary key
- `user_id` (BIGINT, FK) - Reference to users table
- `wallet_number` (VARCHAR(20), UNIQUE) - Unique wallet identifier
- `balance` (DECIMAL(15,2)) - Current wallet balance
- `currency` (VARCHAR(3)) - Currency code (PKR, USD, etc.)
- `wallet_status` (ENUM) - ACTIVE, INACTIVE, FROZEN, BLOCKED
- `wallet_type` (ENUM) - PERSONAL, BUSINESS, SAVINGS
- `created_at` (TIMESTAMP) - Creation date
- `updated_at` (TIMESTAMP) - Last update date

**Relationships:**
- Many-to-One with users (user_id → users.id)
- One-to-Many with transactions
- One-to-One with transaction_limits
- One-to-Many with wallet_notifications

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (user_id)
- UNIQUE (wallet_number)
- INDEX (user_id)
- INDEX (wallet_number)
- INDEX (wallet_status)

**Sample Data:**
```sql
INSERT INTO wallets VALUES
(1, 1, 'WLT-001-2024-0001', 50000.00, 'PKR', 'ACTIVE', 'PERSONAL', NOW(), NOW()),
(2, 2, 'WLT-001-2024-0002', 25000.00, 'PKR', 'ACTIVE', 'PERSONAL', NOW(), NOW());
```

---

### 3. transactions
**Purpose:** Record all wallet transactions

**Columns:**
- `id` (BIGINT, PK) - Auto-increment primary key
- `transaction_ref` (VARCHAR(50), UNIQUE) - Unique transaction reference
- `wallet_id` (BIGINT, FK) - Reference to wallets table
- `transaction_type` (ENUM) - CREDIT, DEBIT, TRANSFER_IN, TRANSFER_OUT, PAYMENT, REFUND, WITHDRAWAL, TOP_UP
- `amount` (DECIMAL(15,2)) - Transaction amount
- `currency` (VARCHAR(3)) - Currency code
- `balance_before` (DECIMAL(15,2)) - Balance before transaction
- `balance_after` (DECIMAL(15,2)) - Balance after transaction
- `transaction_status` (ENUM) - PENDING, COMPLETED, FAILED, CANCELLED, REFUNDED
- `description` (TEXT) - Transaction description
- `recipient_wallet_id` (BIGINT, FK) - For transfers (nullable)
- `payment_method` (VARCHAR(50)) - Payment method used
- `transaction_fee` (DECIMAL(10,2)) - Transaction fee (if any)
- `created_at` (TIMESTAMP) - Transaction initiation time
- `completed_at` (TIMESTAMP) - Transaction completion time

**Relationships:**
- Many-to-One with wallets (wallet_id → wallets.id)
- Many-to-One with wallets (recipient_wallet_id → wallets.id)

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (wallet_id)
- FOREIGN KEY (recipient_wallet_id)
- UNIQUE (transaction_ref)
- INDEX (wallet_id)
- INDEX (transaction_ref)
- INDEX (created_at)
- INDEX (transaction_status)
- COMPOSITE INDEX (created_at, transaction_status)

**Sample Data:**
```sql
INSERT INTO transactions VALUES
(1, 'TXN-2024-11-15-0001', 1, 'TOP_UP', 50000.00, 'PKR', 0.00, 50000.00, 'COMPLETED', 'Initial top-up', NULL, 'BANK_ACCOUNT', 0.00, NOW(), NOW());
```

---

### 4. transaction_limits
**Purpose:** Store transaction limits for each wallet

**Columns:**
- `id` (BIGINT, PK) - Auto-increment primary key
- `wallet_id` (BIGINT, FK, UNIQUE) - Reference to wallets table
- `daily_limit` (DECIMAL(15,2)) - Maximum daily spend
- `monthly_limit` (DECIMAL(15,2)) - Maximum monthly spend
- `per_transaction_limit` (DECIMAL(15,2)) - Maximum per transaction
- `daily_spent` (DECIMAL(15,2)) - Current day spending
- `monthly_spent` (DECIMAL(15,2)) - Current month spending
- `last_reset_date` (DATE) - Last limit reset date
- `created_at` (TIMESTAMP) - Creation date
- `updated_at` (TIMESTAMP) - Last update date

**Relationships:**
- One-to-One with wallets (wallet_id → wallets.id)

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (wallet_id)
- UNIQUE (wallet_id)

**Default Limits:**
- Daily: PKR 50,000
- Monthly: PKR 500,000
- Per Transaction: PKR 25,000

---

### 5. payment_methods
**Purpose:** Store user payment methods

**Columns:**
- `id` (BIGINT, PK) - Auto-increment primary key
- `user_id` (BIGINT, FK) - Reference to users table
- `method_type` (ENUM) - BANK_ACCOUNT, CARD, MOBILE_WALLET, UPI
- `provider_name` (VARCHAR(100)) - Bank/provider name
- `account_number` (VARCHAR(100)) - Account/card number
- `account_holder_name` (VARCHAR(100)) - Account holder name
- `is_default` (BOOLEAN) - Default payment method
- `is_verified` (BOOLEAN) - Verification status
- `created_at` (TIMESTAMP) - Creation date

**Relationships:**
- Many-to-One with users (user_id → users.id)

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (user_id)
- INDEX (user_id)

---

### 6. wallet_notifications
**Purpose:** Store wallet notifications and alerts

**Columns:**
- `id` (BIGINT, PK) - Auto-increment primary key
- `wallet_id` (BIGINT, FK) - Reference to wallets table
- `notification_type` (ENUM) - TRANSACTION, SECURITY, LIMIT, PROMOTIONAL, SYSTEM
- `title` (VARCHAR(200)) - Notification title
- `message` (TEXT) - Notification message
- `is_read` (BOOLEAN) - Read status
- `priority` (ENUM) - LOW, MEDIUM, HIGH
- `created_at` (TIMESTAMP) - Creation date

**Relationships:**
- Many-to-One with wallets (wallet_id → wallets.id)

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (wallet_id)
- INDEX (wallet_id)
- INDEX (created_at)

---

### 7. payment_links
**Purpose:** Store payment collection links

**Columns:**
- `id` (BIGINT, PK) - Auto-increment primary key
- `wallet_id` (BIGINT, FK) - Merchant wallet ID
- `link_code` (VARCHAR(50), UNIQUE) - Unique payment link code
- `amount` (DECIMAL(15,2)) - Payment amount
- `description` (TEXT) - Payment description
- `expiry_date` (TIMESTAMP) - Link expiration time
- `is_used` (BOOLEAN) - Usage status
- `payment_status` (ENUM) - PENDING, COMPLETED, EXPIRED, CANCELLED
- `paid_by_wallet_id` (BIGINT, FK) - Payer wallet (nullable)
- `created_at` (TIMESTAMP) - Creation date
- `used_at` (TIMESTAMP) - Usage date

**Relationships:**
- Many-to-One with wallets (wallet_id → wallets.id)
- Many-to-One with wallets (paid_by_wallet_id → wallets.id)

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (wallet_id)
- FOREIGN KEY (paid_by_wallet_id)
- UNIQUE (link_code)
- INDEX (link_code)

---

### 8. kyc_documents
**Purpose:** Store KYC verification documents

**Columns:**
- `id` (BIGINT, PK) - Auto-increment primary key
- `user_id` (BIGINT, FK) - Reference to users table
- `document_type` (ENUM) - CNIC, PASSPORT, DRIVING_LICENSE, UTILITY_BILL
- `document_number` (VARCHAR(50)) - Document identification number
- `document_path` (VARCHAR(255)) - File storage path
- `verification_status` (ENUM) - PENDING, VERIFIED, REJECTED
- `verified_at` (TIMESTAMP) - Verification date
- `verified_by` (BIGINT, FK) - Admin user ID who verified
- `rejection_reason` (TEXT) - Reason for rejection
- `created_at` (TIMESTAMP) - Upload date

**Relationships:**
- Many-to-One with users (user_id → users.id)
- Many-to-One with users (verified_by → users.id)

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (user_id)
- INDEX (user_id)

---

## 🔗 Relationship Summary

### One-to-Many Relationships
1. **users → wallets** (A user can have multiple wallets)
2. **users → payment_methods** (A user can have multiple payment methods)
3. **wallets → transactions** (A wallet can have many transactions)
4. **wallets → wallet_notifications** (A wallet can have many notifications)
5. **wallets → payment_links** (A wallet can create many payment links)

### One-to-One Relationships
1. **wallets → transaction_limits** (Each wallet has one limit configuration)

### Many-to-One Relationships
1. **transactions → wallets** (Many transactions belong to one wallet)
2. **transactions → wallets (recipient)** (For transfer transactions)

---

## 📈 Database Views

### daily_transaction_summary
**Purpose:** Quick access to daily transaction summaries

```sql
CREATE VIEW daily_transaction_summary AS
SELECT 
    DATE(created_at) as transaction_date,
    COUNT(*) as total_transactions,
    SUM(CASE WHEN transaction_type IN ('CREDIT', 'TOP_UP', 'TRANSFER_IN') THEN amount ELSE 0 END) as total_credits,
    SUM(CASE WHEN transaction_type IN ('DEBIT', 'PAYMENT', 'WITHDRAWAL', 'TRANSFER_OUT') THEN amount ELSE 0 END) as total_debits
FROM transactions
WHERE transaction_status = 'COMPLETED'
GROUP BY DATE(created_at);
```

### wallet_summary
**Purpose:** Consolidated wallet information with statistics

```sql
CREATE VIEW wallet_summary AS
SELECT 
    w.id,
    w.wallet_number,
    u.full_name,
    w.balance,
    w.wallet_status,
    COUNT(t.id) as total_transactions
FROM wallets w
JOIN users u ON w.user_id = u.id
LEFT JOIN transactions t ON w.id = t.wallet_id
GROUP BY w.id;
```

---

## 🔐 Data Integrity & Constraints

### Primary Keys
- All tables have auto-increment BIGINT primary keys
- Ensures unique identification of records

### Foreign Keys
- ON DELETE CASCADE for dependent data (wallets → transactions)
- ON DELETE SET NULL for optional references (recipient_wallet_id)
- Maintains referential integrity

### Unique Constraints
- username, email (users table)
- wallet_number (wallets table)
- transaction_ref (transactions table)
- link_code (payment_links table)

### Check Constraints
- balance >= 0 (logical constraint in application)
- amount > 0 for transactions (logical constraint)
- daily_spent <= daily_limit (logical constraint)

---

## 📊 Sample Query Examples

### Get Wallet Balance with Transaction Count
```sql
SELECT 
    w.wallet_number,
    w.balance,
    COUNT(t.id) as transaction_count,
    COALESCE(SUM(CASE WHEN t.transaction_status = 'COMPLETED' THEN t.amount ELSE 0 END), 0) as total_transacted
FROM wallets w
LEFT JOIN transactions t ON w.id = t.wallet_id
WHERE w.id = 1
GROUP BY w.id, w.wallet_number, w.balance;
```

### Get User's Total Balance
```sql
SELECT 
    u.full_name,
    SUM(w.balance) as total_balance
FROM users u
JOIN wallets w ON u.id = w.user_id
WHERE u.id = 1
GROUP BY u.id, u.full_name;
```

### Get Monthly Spending
```sql
SELECT 
    w.wallet_number,
    YEAR(t.created_at) as year,
    MONTH(t.created_at) as month,
    SUM(t.amount) as monthly_spend
FROM transactions t
JOIN wallets w ON t.wallet_id = w.id
WHERE t.transaction_type IN ('DEBIT', 'PAYMENT', 'WITHDRAWAL', 'TRANSFER_OUT')
AND t.transaction_status = 'COMPLETED'
GROUP BY w.wallet_number, YEAR(t.created_at), MONTH(t.created_at);
```

---

## 🎯 Database Best Practices Implemented

✅ Normalization (3NF)  
✅ Proper indexing for performance  
✅ Foreign key constraints  
✅ Timestamp tracking (created_at, updated_at)  
✅ Enum types for status fields  
✅ Decimal precision for financial data  
✅ Unique constraints on business keys  
✅ Views for complex queries  
✅ Sample data for testing  
✅ Cascading deletes where appropriate

---

**Database design is optimized for:**
- Fast querying
- Data integrity
- Scalability
- Transaction safety
- Audit capability
