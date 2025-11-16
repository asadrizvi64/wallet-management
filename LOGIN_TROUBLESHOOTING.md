# Login Authentication Fix - Troubleshooting Guide

## 🚨 Problem Description

Users unable to login after registration with error: **"Invalid email or password"** even with correct credentials.

## 🔍 Root Cause

The issue was caused by **NULL or FALSE values in the `is_active` column** of the users table. Spring Security's authentication flow works as follows:

1. User attempts login with email/password
2. `CustomUserDetailsService.loadUserByUsername()` loads the user from database
3. `UserPrincipal.isEnabled()` is called to check if account is enabled
4. **If `isEnabled()` returns `false`, Spring Security throws `DisabledException` BEFORE checking password**
5. This appears to the user as "Invalid email or password" error

### Why isEnabled() returns false:

```java
// From UserPrincipal.java line 69-71
@Override
public boolean isEnabled() {
    return isActive != null && isActive;  // Returns false if isActive is NULL!
}
```

If `is_active` is NULL in the database, `isEnabled()` returns `false`, blocking login.

## ✅ Quick Fix

### Option 1: Run the automated fix script

**Linux/Mac:**
```bash
./FIX_LOGIN_NOW.sh
```

**Windows:**
```batch
FIX_LOGIN_NOW.bat
```

### Option 2: Manual database fix

1. Connect to your database:
```bash
mysql -h localhost -u root -p
# OR
mariadb -h localhost -u root -p
```

2. Run the fix script:
```sql
source COMPLETE_LOGIN_FIX.sql
```

3. Verify all users show "CAN LOGIN" in the output

### Option 3: Direct SQL commands

```sql
USE wallet_db;

-- Fix NULL is_active values
UPDATE users SET is_active = TRUE WHERE is_active IS NULL;

-- Fix inactive users (optional - enables ALL users)
UPDATE users SET is_active = TRUE WHERE is_active = FALSE;

-- Add constraint to prevent future NULL values
ALTER TABLE users MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- Verify fix
SELECT id, username, email, is_active FROM users;
```

## 🔄 After Applying the Fix

1. **Restart the backend application** (important!)
   ```bash
   # Stop the backend
   docker-compose down
   # OR
   ./dev-stop.sh

   # Start the backend
   docker-compose up -d
   # OR
   ./dev-start.sh
   ```

2. **Clear browser cache/local storage** (if using web frontend)

3. **Try logging in** with your registered credentials

## 🧪 Testing the Fix

### Test with sample users (from schema.sql):

| Email | Password | Role |
|-------|----------|------|
| asad@example.com | password123 | SUPERUSER |
| ali@example.com | password123 | USER |
| sara@example.com | password123 | USER |

### Test with your registered users:

Use the email and password you used during registration.

## 🐛 If Login Still Fails

### 1. Check backend logs

The application has DEBUG logging enabled for Spring Security:

```bash
# View backend logs
docker-compose logs -f backend
# OR
# Check your application console output
```

Look for:
- `DisabledException: User is disabled`
- `BadCredentialsException: Bad credentials`
- `UsernameNotFoundException: User not found`

### 2. Verify user exists in database

```sql
USE wallet_db;
SELECT id, username, email, is_active, user_role FROM users WHERE email = 'your@email.com';
```

Check:
- ✅ User exists
- ✅ `is_active` is `1` (TRUE)
- ✅ `user_role` is set (not NULL)

### 3. Verify password is encrypted

```sql
SELECT email, password FROM users WHERE email = 'your@email.com';
```

The password should start with `$2a$` (BCrypt hash). Example:
```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

If password doesn't start with `$2a$`, registration is broken.

### 4. Test the API directly

Using curl:
```bash
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "asad@example.com",
    "password": "password123"
  }'
```

Expected success response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": 1,
    "email": "asad@example.com",
    "token": "eyJhbGc..."
  }
}
```

Expected error response (if still failing):
```json
{
  "success": false,
  "message": "Invalid email or password"
}
```

### 5. Check Flyway migrations

```sql
USE wallet_db;
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

You should see:
- ✅ V1__fix_user_role_and_add_cnic (SUCCESS)
- ✅ V2__fix_is_active_null_values (SUCCESS)

If V2 shows FAILED or is missing:
```sql
-- Remove failed entry
DELETE FROM flyway_schema_history WHERE version = '2';

-- Restart backend to re-run migrations
```

## 🛡️ Prevention

This fix is permanent. The database migration `V2__fix_is_active_null_values.sql` ensures:

1. All existing NULL values are set to TRUE
2. The column is set to NOT NULL with DEFAULT TRUE
3. Future user registrations will automatically have `is_active = TRUE`

## 📋 Common Error Messages

| Error Message | Cause | Fix |
|--------------|-------|-----|
| "Invalid email or password" | `is_active` is NULL or FALSE | Run COMPLETE_LOGIN_FIX.sql |
| "User not found" | Email/username doesn't exist | Register first |
| "Account is deactivated" | `is_active = FALSE` | Run: `UPDATE users SET is_active = TRUE WHERE email = 'your@email.com';` |
| 401 Unauthorized | Token expired or invalid | Login again to get new token |

## 📞 Still Having Issues?

1. Check that MySQL/MariaDB is running:
   ```bash
   docker-compose ps
   # OR
   systemctl status mysql
   ```

2. Check backend is running:
   ```bash
   curl http://localhost:8080/api/v1/users/register
   # Should return 400 (not 404 or connection refused)
   ```

3. Check database connection in backend logs:
   ```bash
   grep -i "database\|connection\|jdbc" backend_logs.txt
   ```

4. Verify database credentials in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/wallet_db
   spring.datasource.username=root
   spring.datasource.password=root
   ```

## 📚 Related Files

- `COMPLETE_LOGIN_FIX.sql` - Comprehensive database fix script
- `FIX_LOGIN_NOW.sh` - Automated fix for Linux/Mac
- `FIX_LOGIN_NOW.bat` - Automated fix for Windows
- `backend/src/main/resources/db/migration/V2__fix_is_active_null_values.sql` - Flyway migration
- `backend/src/main/java/com/enterprise/wallet/security/UserPrincipal.java` - Authentication logic

## ✨ Summary

The login issue was caused by NULL `is_active` values preventing Spring Security from authenticating users. The fix scripts set all users to active and prevent future NULL values. **After running the fix, restart your backend application and try logging in again.**
