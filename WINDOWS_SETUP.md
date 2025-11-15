# Windows Local Development Setup

This guide provides direct CLI commands for running the Wallet Management System on Windows without using the batch files.

## Prerequisites Check

Run these commands in PowerShell or Command Prompt to verify your setup:

```cmd
java -version
mvn -version
node -v
npm -v
docker --version
mysql --version
```

If any command fails, install the missing software:
- **Java 17+**: https://adoptium.net/
- **Maven 3.6+**: https://maven.apache.org/download.cgi
- **Node.js 16+**: https://nodejs.org/
- **MySQL 8.0**: https://dev.mysql.com/downloads/mysql/
- **Docker** (optional): https://docs.docker.com/desktop/install/windows-install/

---

## Option 1: Docker Setup (Recommended)

### Start Services
```cmd
cd C:\path\to\wallet-management
docker compose up -d
```

### Check Status
```cmd
docker compose ps
```

### View Logs
```cmd
docker compose logs -f
```

### Stop Services
```cmd
docker compose down
```

### Access Points
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- MySQL: localhost:3306

---

## Option 2: Local Development (Without Docker)

### Step 1: Setup MySQL Database

```cmd
# Start MySQL service (if not running)
net start MySQL80

# Login to MySQL
mysql -u root -p

# In MySQL console, create database:
CREATE DATABASE IF NOT EXISTS wallet_db;
USE wallet_db;
SOURCE C:\path\to\wallet-management\backend\src\main\resources\schema.sql;
EXIT;
```

Or let the application auto-create the database (configured in application.properties).

### Step 2: Start Backend

Open a new Command Prompt/PowerShell window:

```cmd
# Navigate to backend directory
cd C:\path\to\wallet-management\backend

# Clean and install dependencies (first time only)
mvn clean install

# Start Spring Boot application
mvn spring-boot:run
```

**Backend will start on**: http://localhost:8080

**Swagger UI**: http://localhost:8080/swagger-ui.html

### Step 3: Start Frontend

Open another Command Prompt/PowerShell window:

```cmd
# Navigate to frontend directory
cd C:\path\to\wallet-management\frontend

# Install dependencies (first time only)
npm install

# Start React development server
npm start
```

**Frontend will start on**: http://localhost:3000

### Step 4: Test the Application

**Default Test Accounts:**
- Username: `asad_khan` | Password: `password123`
- Username: `ali_ahmed` | Password: `password123`

---

## Troubleshooting

### Port Already in Use

**Backend (8080):**
```cmd
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

**Frontend (3000):**
```cmd
# Find process using port 3000
netstat -ano | findstr :3000

# Kill the process
taskkill /PID <PID> /F
```

### MySQL Connection Issues

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/wallet_db
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### Maven Build Issues

```cmd
# Clear Maven cache and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

### Node/npm Issues

```cmd
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rmdir /s /q node_modules
del package-lock.json
npm install
```

---

## Running Individual Commands

### Backend Only

```cmd
cd backend
mvn spring-boot:run
```

### Frontend Only

```cmd
cd frontend
npm start
```

### Build Production

```cmd
# Backend JAR
cd backend
mvn clean package

# Frontend Build
cd frontend
npm run build
```

---

## Stopping Services

### Development Mode

Press `Ctrl + C` in each terminal window where services are running.

### Docker Mode

```cmd
docker compose down
```

### Force Kill All Java/Node Processes (Use Carefully)

```cmd
# Kill all Java processes
taskkill /F /IM java.exe

# Kill all Node processes
taskkill /F /IM node.exe
```

---

## Quick Reference

| Task | Command |
|------|---------|
| Start MySQL | `net start MySQL80` |
| Stop MySQL | `net stop MySQL80` |
| Backend Dev | `cd backend && mvn spring-boot:run` |
| Frontend Dev | `cd frontend && npm start` |
| Docker Up | `docker compose up -d` |
| Docker Down | `docker compose down` |
| View Logs | `docker compose logs -f` |
| Build Backend | `cd backend && mvn clean package` |
| Build Frontend | `cd frontend && npm run build` |

---

## Development Workflow

1. **First Time Setup:**
   ```cmd
   cd wallet-management
   cd backend && mvn clean install && cd ..
   cd frontend && npm install && cd ..
   ```

2. **Daily Development:**
   ```cmd
   # Terminal 1: Backend
   cd backend
   mvn spring-boot:run

   # Terminal 2: Frontend
   cd frontend
   npm start
   ```

3. **Testing:**
   - Import Postman collection from `postman/` directory
   - Test API endpoints via Swagger UI
   - Use frontend dashboard

4. **Stopping:**
   - Press `Ctrl + C` in each terminal

---

## Environment Variables (Optional)

Create a `.env` file in the backend directory:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/wallet_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password
SERVER_PORT=8080
```

---

## Additional Resources

- **README.md**: Complete project documentation
- **Postman Collection**: `postman/Wallet_Management_APIs.postman_collection.json`
- **Database Schema**: `backend/src/main/resources/schema.sql`
- **Swagger UI**: http://localhost:8080/swagger-ui.html

---

## Need Help?

1. Check application logs in the terminal
2. Review Swagger documentation
3. Verify database connection
4. Check if ports 3000 and 8080 are available
5. Ensure all prerequisites are installed correctly
