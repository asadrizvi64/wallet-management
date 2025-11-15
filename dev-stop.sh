#!/bin/bash

###############################################################################
# Wallet Management System - Stop Development Servers
# This script stops the backend and frontend development servers
###############################################################################

echo "=========================================="
echo "Stopping Development Servers"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Try to read PIDs from file
if [ -f "logs/backend.pid" ] && [ -f "logs/frontend.pid" ]; then
    BACKEND_PID=$(cat logs/backend.pid)
    FRONTEND_PID=$(cat logs/frontend.pid)

    echo -e "${YELLOW}Stopping processes by PID...${NC}"

    if kill -0 $BACKEND_PID 2>/dev/null; then
        kill $BACKEND_PID
        echo -e "${GREEN}✓ Backend stopped (PID: $BACKEND_PID)${NC}"
    else
        echo -e "${YELLOW}⚠ Backend process not found (PID: $BACKEND_PID)${NC}"
    fi

    if kill -0 $FRONTEND_PID 2>/dev/null; then
        kill $FRONTEND_PID
        echo -e "${GREEN}✓ Frontend stopped (PID: $FRONTEND_PID)${NC}"
    else
        echo -e "${YELLOW}⚠ Frontend process not found (PID: $FRONTEND_PID)${NC}"
    fi

    # Clean up PID files
    rm -f logs/backend.pid logs/frontend.pid
else
    echo -e "${YELLOW}PID files not found. Attempting to kill by process name...${NC}"
fi

# Fallback: Kill by process name
echo ""
echo -e "${YELLOW}Cleaning up any remaining processes...${NC}"

# Kill Spring Boot
pkill -f 'spring-boot:run' 2>/dev/null && echo -e "${GREEN}✓ Spring Boot processes stopped${NC}"

# Kill React dev server
pkill -f 'react-scripts' 2>/dev/null && echo -e "${GREEN}✓ React dev server processes stopped${NC}"

# Kill npm processes
pkill -f 'npm start' 2>/dev/null && echo -e "${GREEN}✓ npm processes stopped${NC}"

# Kill node processes related to the frontend
pkill -f 'node.*frontend' 2>/dev/null

# Kill any Java processes running Maven
pkill -f 'mvn.*spring-boot:run' 2>/dev/null

echo ""
echo "=========================================="
echo -e "${GREEN}✓ Development servers stopped!${NC}"
echo "=========================================="
echo ""
echo "To start again, run: ./dev-start.sh"
echo ""
