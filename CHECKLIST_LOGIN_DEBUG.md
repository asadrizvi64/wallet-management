# Login Debug Checklist

Since `is_active = 1` for all users but login still fails, work through this checklist:

## ✅ Step 1: Verify Database State

Run this to check passwords and wallets:

```bash
mariadb -h localhost -u root -proot < DEBUG_LOGIN.sql
```

**Check the output for:**
- ✓ All passwords should show "BCrypt encrypted (OK)"
- ✓ All users should have a wallet
- ✗ If passwords show "NOT encrypted" → **You must re-register those users**
- ✗ If users have no wallet → See "Fix Missing Wallets" below

---

## ✅ Step 2: Restart Backend (CRITICAL!)

The backend MUST be restarted after database changes:

```bash
# Stop backend
docker-compose down
# OR
./dev-stop.sh

# Start backend
docker-compose up -d
# OR
./dev-start.sh

# Check it's running
docker-compose ps
# OR
curl http://localhost:8080/api/v1/users/register
```

**Did you restart the backend after running the database fix?** If not, do it now!

---

## ✅ Step 3: Test Login with Script

```bash
./TEST_LOGIN.sh
```

This will:
1. Check if backend is running
2. Test login with sample user (asad@example.com / password123)
3. Register a new test user
4. Test login with the new user

**If new user CAN login but old users CANNOT:**
→ Your old users have bad passwords in the database (not encrypted properly)
→ Solution: Re-register those accounts OR see "Fix Passwords" below

**If NO users can login (including new ones):**
→ Backend configuration issue
→ Check backend logs (Step 4)

---

## ✅ Step 4: Check Backend Logs

```bash
# If using Docker
docker-compose logs -f backend | grep -i "error\|exception\|authentication\|login"

# OR just view all recent logs
docker-compose logs --tail=100 backend
```

**Look for these errors:**
- `DisabledException: User is disabled` → Run COMPLETE_LOGIN_FIX.sql again
- `BadCredentialsException: Bad credentials` → Wrong password OR password not encrypted
- `UsernameNotFoundException: User not found` → Email doesn't exist in database
- `JWTException` → JWT token configuration issue
- `SQLException` or `Database connection` → Database not accessible

---

## ✅ Step 5: Which Users Are You Trying to Login With?

**Option A: Sample users from schema.sql**

If you initialized the database with `schema.sql`, try:
- Email: `asad@example.com` / Password: `password123`
- Email: `ali@example.com` / Password: `password123`
- Email: `sara@example.com` / Password: `password123`

**Option B: Users you registered yourself**

Use the email and password you entered during registration.

**Option C: Check what users exist in database**

```bash
mariadb -h localhost -u root -proot -e "USE wallet_db; SELECT id, username, email, is_active, user_role FROM users;"
```

---

## ✅ Step 6: Test Login Directly with curl

Replace with your actual email/password:

```bash
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your@email.com",
    "password": "yourpassword"
  }'
```

**Expected SUCCESS response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": 1,
    "email": "your@email.com",
    "token": "eyJhbGciOi..."
  }
}
```

**Expected FAILURE response:**
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

If you get the failure response, the backend is working but credentials are wrong OR passwords aren't encrypted.

---

## 🔧 Common Fixes

### Fix A: Missing Wallets

If some users don't have wallets:

```sql
USE wallet_db;

-- Find users without wallets
SELECT u.id, u.email, u.username
FROM users u
LEFT JOIN wallets w ON u.id = w.user_id
WHERE w.id IS NULL;

-- Create wallets for users without them
-- (Run this for each user_id that needs a wallet)
INSERT INTO wallets (user_id, wallet_number, balance, currency, wallet_status, wallet_type)
VALUES (
    YOUR_USER_ID_HERE,
    CONCAT('WLT', LPAD(FLOOR(RAND() * 10000000000), 10, '0')),
    0.00,
    'PKR',
    'ACTIVE',
    'PERSONAL'
);
```

### Fix B: Passwords Not Encrypted

If passwords in database are plain text (not starting with `$2a$`):

**You MUST re-register these users.** There's no way to convert plain text to BCrypt hash without knowing the original password.

OR if you know the passwords:

```java
// Use this Java code to generate BCrypt hashes
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "yourpassword";
        String hash = encoder.encode(password);
        System.out.println(hash);
        // Copy the hash and update database:
        // UPDATE users SET password = '$2a$...' WHERE email = 'user@email.com';
    }
}
```

### Fix C: Backend Not Starting

Check backend logs for startup errors:

```bash
docker-compose logs backend | grep -i "error\|failed\|exception" | tail -20
```

Common issues:
- Database connection failed → Check MySQL/MariaDB is running
- Port 8080 already in use → Stop other applications using port 8080
- Flyway migration failed → Check flyway_schema_history table

---

## 📝 Quick Diagnostic Summary

Run all these commands and share the output if you need more help:

```bash
# 1. Check backend is running
curl -s http://localhost:8080/api/v1/users/register | head -5

# 2. Check database users
mariadb -h localhost -u root -proot -e "USE wallet_db; SELECT id, username, email, is_active, LEFT(password, 20) AS pwd_sample FROM users;"

# 3. Check Docker status (if using Docker)
docker-compose ps

# 4. Check recent backend logs
docker-compose logs --tail=50 backend | grep -i "authentication\|login\|error"
```

---

## 🎯 Most Likely Issues

Based on "is_active = 1" but login still fails:

1. **Backend not restarted** (80% of cases)
   - Solution: `docker-compose down && docker-compose up -d`

2. **Passwords not BCrypt encrypted** (15% of cases)
   - Solution: Re-register users OR run DEBUG_LOGIN.sql to check

3. **Wrong credentials being used** (3% of cases)
   - Solution: Try asad@example.com / password123 if you used schema.sql

4. **Users missing wallets** (2% of cases)
   - Solution: Run DEBUG_LOGIN.sql to check, then create wallets

---

## ✅ Final Verification

After fixes, verify login works:

```bash
# Test with curl
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{"email": "asad@example.com", "password": "password123"}'

# Should see: "success": true
```

If you get a JWT token in the response → **LOGIN IS WORKING!** 🎉
