#!/bin/bash
# Tank War Game Launcher

echo "Starting Tank War Game..."
echo "================================"
echo ""

cd "$(dirname "$0")"

# Run the game with Maven
mvn clean javafx:run

echo ""
echo "Game ended. Press any key to exit..."
read -n 1

