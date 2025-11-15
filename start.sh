#!/bin/bash

###############################################################################
# Wallet Management System - Startup Script
# This script starts the entire application using Docker Compose
###############################################################################

echo "=================================="
echo "Wallet Management System - Startup"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    echo "Please install Docker from https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo -e "${RED}Error: Docker Compose is not installed${NC}"
    echo "Please install Docker Compose from https://docs.docker.com/compose/install/"
    exit 1
fi

echo -e "${YELLOW}Starting services...${NC}"
echo ""

# Start Docker Compose services
if command -v docker-compose &> /dev/null; then
    docker-compose up -d
else
    docker compose up -d
fi

# Check if services started successfully
if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Services started successfully!${NC}"
    echo ""
    echo "=================================="
    echo "Access your application at:"
    echo "=================================="
    echo -e "${GREEN}Frontend (React):${NC}     http://localhost:3000"
    echo -e "${GREEN}Backend API:${NC}          http://localhost:8080"
    echo -e "${GREEN}Swagger UI:${NC}           http://localhost:8080/swagger-ui.html"
    echo -e "${GREEN}MySQL Database:${NC}       localhost:3306"
    echo ""
    echo "=================================="
    echo "Default Test Accounts:"
    echo "=================================="
    echo "Username: asad_khan"
    echo "Password: password123"
    echo ""
    echo "Username: ali_ahmed"
    echo "Password: password123"
    echo ""
    echo "=================================="
    echo -e "${YELLOW}Useful Commands:${NC}"
    echo "=================================="
    echo "Stop services:        ./stop.sh"
    echo "View logs:            docker-compose logs -f"
    echo "Check status:         docker-compose ps"
    echo "Restart services:     docker-compose restart"
    echo ""
else
    echo -e "${RED}✗ Failed to start services${NC}"
    echo "Please check the error messages above"
    exit 1
fi
