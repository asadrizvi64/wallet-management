#!/bin/bash

###############################################################################
# Wallet Management System - Stop Script
# This script stops all running Docker containers
###############################################################################

echo "=================================="
echo "Wallet Management System - Shutdown"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Stopping services...${NC}"
echo ""

# Stop Docker Compose services
if command -v docker-compose &> /dev/null; then
    docker-compose down
else
    docker compose down
fi

# Check if services stopped successfully
if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ All services stopped successfully!${NC}"
    echo ""
    echo "=================================="
    echo "To start again, run: ./start.sh"
    echo ""
    echo "To remove all data (including database):"
    echo "docker-compose down -v"
    echo "=================================="
    echo ""
else
    echo -e "${RED}✗ Failed to stop services${NC}"
    echo "Please check the error messages above"
    exit 1
fi
