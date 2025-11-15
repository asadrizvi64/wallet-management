#!/bin/bash

###############################################################################
# Wallet Management System - Development Mode Startup
# This script starts backend and frontend in development mode (without Docker)
###############################################################################

echo "=========================================="
echo "Wallet Management System - Development Mode"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check prerequisites
echo -e "${YELLOW}Checking prerequisites...${NC}"
echo ""

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ Java is not installed${NC}"
    echo "Please install Java 17 or higher"
    exit 1
else
    echo -e "${GREEN}✓ Java found:${NC} $(java -version 2>&1 | head -n 1)"
fi

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven is not installed${NC}"
    echo "Please install Maven 3.6 or higher"
    exit 1
else
    echo -e "${GREEN}✓ Maven found:${NC} $(mvn -version | head -n 1)"
fi

# Check Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}✗ Node.js is not installed${NC}"
    echo "Please install Node.js 16 or higher"
    exit 1
else
    echo -e "${GREEN}✓ Node.js found:${NC} $(node -v)"
fi

# Check npm
if ! command -v npm &> /dev/null; then
    echo -e "${RED}✗ npm is not installed${NC}"
    echo "Please install npm"
    exit 1
else
    echo -e "${GREEN}✓ npm found:${NC} $(npm -v)"
fi

# Check MySQL
if ! command -v mysql &> /dev/null; then
    echo -e "${YELLOW}⚠ MySQL client not found in PATH${NC}"
    echo "Make sure MySQL server is running on localhost:3306"
else
    echo -e "${GREEN}✓ MySQL client found${NC}"
fi

echo ""
echo "=========================================="
echo -e "${BLUE}Prerequisites check completed!${NC}"
echo "=========================================="
echo ""

# Ask user if they want to install frontend dependencies
if [ ! -d "frontend/node_modules" ]; then
    echo -e "${YELLOW}Frontend dependencies not found.${NC}"
    echo -e "${YELLOW}Installing npm packages...${NC}"
    cd frontend
    npm install
    cd ..
    echo ""
fi

echo "=========================================="
echo -e "${YELLOW}Starting services...${NC}"
echo "=========================================="
echo ""

# Create logs directory if it doesn't exist
mkdir -p logs

echo -e "${BLUE}Starting MySQL (if using Docker):${NC}"
echo "If you want to use MySQL in Docker, run:"
echo "docker run -d --name wallet-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wallet_db -p 3306:3306 mysql:8.0"
echo ""
echo -e "${YELLOW}Press Enter to continue when MySQL is ready...${NC}"
read -r

echo ""
echo -e "${BLUE}1. Starting Backend (Spring Boot)...${NC}"
cd backend
mvn spring-boot:run > ../logs/backend.log 2>&1 &
BACKEND_PID=$!
cd ..
echo -e "${GREEN}   Backend started (PID: $BACKEND_PID)${NC}"
echo "   Logs: logs/backend.log"

echo ""
echo -e "${BLUE}2. Starting Frontend (React)...${NC}"
cd frontend
npm start > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..
echo -e "${GREEN}   Frontend started (PID: $FRONTEND_PID)${NC}"
echo "   Logs: logs/frontend.log"

echo ""
echo "=========================================="
echo -e "${GREEN}✓ Development servers started!${NC}"
echo "=========================================="
echo ""
echo "Access your application:"
echo "=========================================="
echo -e "${GREEN}Frontend (React):${NC}     http://localhost:3000"
echo -e "${GREEN}Backend API:${NC}          http://localhost:8080"
echo -e "${GREEN}Swagger UI:${NC}           http://localhost:8080/swagger-ui.html"
echo ""
echo "Process IDs:"
echo "=========================================="
echo "Backend PID:  $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo ""
echo "To stop the servers:"
echo "=========================================="
echo "kill $BACKEND_PID $FRONTEND_PID"
echo ""
echo "Or use: pkill -f 'spring-boot:run' && pkill -f 'react-scripts'"
echo ""
echo "View logs:"
echo "=========================================="
echo "Backend:  tail -f logs/backend.log"
echo "Frontend: tail -f logs/frontend.log"
echo ""

# Save PIDs to file for easy cleanup
echo "$BACKEND_PID" > logs/backend.pid
echo "$FRONTEND_PID" > logs/frontend.pid

echo -e "${YELLOW}Waiting for services to start...${NC}"
echo "This may take 30-60 seconds..."
echo ""
sleep 5

echo "=========================================="
echo -e "${GREEN}Development environment is ready!${NC}"
echo "=========================================="
