# DATABASE ENTITY RELATIONSHIP DIAGRAM (ERD)
## Wallet Management System

## Visual ERD Representation

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          USERS TABLE                                     │
├─────────────────────────────────────────────────────────────────────────┤
│ Column Name      │ Data Type     │ Constraints                          │
├──────────────────┼───────────────┼──────────────────────────────────────┤
│ user_id          │ BIGINT        │ PRIMARY KEY, AUTO_INCREMENT          │
│ email            │ VARCHAR(255)  │ NOT NULL, UNIQUE                     │
│ password         │ VARCHAR(255)  │ NOT NULL                              │
│ full_name        │ VARCHAR(255)  │ NOT NULL                              │
│ phone_number     │ VARCHAR(20)   │ UNIQUE                                │
│ cnic_number      │ VARCHAR(20)   │ UNIQUE                                │
│ kyc_status       │ VARCHAR(20)   │ DEFAULT 'PENDING'                     │
│ role             │ VARCHAR(20)   │ DEFAULT 'USER'                        │
│ is_active        │ BOOLEAN       │ DEFAULT TRUE                          │
│ created_at       │ TIMESTAMP     │ DEFAULT CURRENT_TIMESTAMP             │
│ updated_at       │ TIMESTAMP     │ AUTO UPDATE                           │
└──────────────────┴───────────────┴──────────────────────────────────────┘
                              │
                              │ ONE-TO-ONE
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         WALLETS TABLE                                    │
├─────────────────────────────────────────────────────────────────────────┤
│ Column Name      │ Data Type     │ Constraints                          │
├──────────────────┼───────────────┼──────────────────────────────────────┤
│ wallet_id        │ BIGINT        │ PRIMARY KEY, AUTO_INCREMENT          │
│ wallet_number    │ VARCHAR(50)   │ NOT NULL, UNIQUE                     │
│ user_id          │ BIGINT        │ FOREIGN KEY → users(user_id)         │
│ balance          │ DECIMAL(15,2) │ DEFAULT 0.00                          │
│ currency         │ VARCHAR(10)   │ DEFAULT 'PKR'                         │
│ status           │ VARCHAR(20)   │ DEFAULT 'ACTIVE'                      │
│ daily_limit      │ DECIMAL(15,2) │ DEFAULT 100000.00                     │
│ monthly_limit    │ DECIMAL(15,2) │ DEFAULT 500000.00                     │
│ daily_spent      │ DECIMAL(15,2) │ DEFAULT 0.00                          │
│ monthly_spent    │ DECIMAL(15,2) │ DEFAULT 0.00                          │
│ created_at       │ TIMESTAMP     │ DEFAULT CURRENT_TIMESTAMP             │
│ updated_at       │ TIMESTAMP     │ AUTO UPDATE                           │
└──────────────────┴───────────────┴──────────────────────────────────────┘
                              │
                              │ ONE-TO-MANY
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      TRANSACTIONS TABLE                                  │
├─────────────────────────────────────────────────────────────────────────┤
│ Column Name          │ Data Type     │ Constraints                      │
├──────────────────────┼───────────────┼──────────────────────────────────┤
│ transaction_id       │ BIGINT        │ PRIMARY KEY, AUTO_INCREMENT      │
│ transaction_reference│ VARCHAR(50)   │ NOT NULL, UNIQUE                 │
│ sender_wallet_id     │ BIGINT        │ FOREIGN KEY → wallets(wallet_id) │
│ receiver_wallet_id   │ BIGINT        │ FOREIGN KEY → wallets(wallet_id) │
│ amount               │ DECIMAL(15,2) │ NOT NULL                          │
│ fee                  │ DECIMAL(15,2) │ DEFAULT 0.00                      │
│ total_amount         │ DECIMAL(15,2) │ NOT NULL                          │
│ type                 │ VARCHAR(20)   │ NOT NULL                          │
│ status               │ VARCHAR(20)   │ DEFAULT 'PENDING'                 │
│ description          │ TEXT          │                                   │
│ payment_gateway_ref  │ VARCHAR(100)  │                                   │
│ created_at           │ TIMESTAMP     │ DEFAULT CURRENT_TIMESTAMP         │
│ completed_at         │ TIMESTAMP     │ NULL                              │
└──────────────────────┴───────────────┴──────────────────────────────────┘
```

## Relationships

### 1. Users ↔ Wallets (1:1)
- Each user has exactly ONE wallet
- Each wallet belongs to exactly ONE user
- Cascade delete: Deleting user deletes associated wallet

### 2. Wallets ↔ Transactions (1:N)
- Each wallet can have MANY transactions
- Each transaction involves ONE or TWO wallets (sender/receiver)
- Transactions can be:
  - DEPOSIT: receiver_wallet_id only
  - WITHDRAWAL: sender_wallet_id only
  - TRANSFER: both sender_wallet_id and receiver_wallet_id

## Indexes

### USERS Table
- `idx_email` on email (for login queries)
- `idx_phone` on phone_number (for uniqueness checks)

### WALLETS Table
- `idx_wallet_number` on wallet_number (for balance queries)
- `idx_user_id` on user_id (for user-wallet lookups)

### TRANSACTIONS Table
- `idx_transaction_ref` on transaction_reference (for status checks)
- `idx_sender` on sender_wallet_id (for sender history)
- `idx_receiver` on receiver_wallet_id (for receiver history)
- `idx_created_at` on created_at (for date-range queries)
- `idx_type` on type (for filtering by transaction type)
- `idx_status` on status (for pending transactions)

## Sample Data Flow

### Transaction Example: Money Transfer

```
User A (user_id: 1)
    ↓
Wallet A (wallet_id: 1, balance: 10000)
    ↓
Transfer Rs. 5000 to Wallet B
    ↓
Transaction Created:
  - sender_wallet_id: 1
  - receiver_wallet_id: 2
  - amount: 5000
  - fee: 10
  - total_amount: 5010
  - type: TRANSFER
    ↓
Wallet A balance: 10000 - 5010 = 4990
Wallet B balance: 5000 + 5000 = 10000
```

## Data Integrity Rules

1. **Balance Constraints:**
   - Balance cannot be negative
   - Total_amount = amount + fee

2. **Transaction Types:**
   - DEPOSIT: Only receiver_wallet_id
   - WITHDRAWAL: Only sender_wallet_id
   - TRANSFER: Both wallets required
   - PAYMENT: Both wallets required
   - REFUND: Reverse of original transaction

3. **Status Flow:**
   - PENDING → PROCESSING → COMPLETED
   - PENDING → FAILED
   - COMPLETED → REVERSED (for refunds)

4. **Wallet Status:**
   - ACTIVE: Can perform all operations
   - FROZEN: Cannot perform any operations
   - SUSPENDED: Admin review required
   - CLOSED: Permanently disabled

## Database Views

### wallet_summary VIEW
```sql
CREATE VIEW wallet_summary AS
SELECT 
    u.user_id,
    u.full_name,
    u.email,
    w.wallet_number,
    w.balance,
    w.status,
    COUNT(t.transaction_id) as total_transactions
FROM users u
JOIN wallets w ON u.user_id = w.user_id
LEFT JOIN transactions t ON 
    w.wallet_id = t.sender_wallet_id OR 
    w.wallet_id = t.receiver_wallet_id
GROUP BY u.user_id;
```

This provides a quick overview of each user's wallet status and activity.
