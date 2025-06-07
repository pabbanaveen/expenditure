#!/bin/bash

# Chitti Manager Deployment Script

echo "=== Chitti Manager Deployment ==="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 17 or later."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven 3.6 or later."
    exit 1
fi

# Check if MongoDB is running
if ! pgrep mongod > /dev/null; then
    echo "MongoDB is not running. Please start MongoDB service."
    exit 1
fi

echo "✓ Prerequisites check passed"

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✓ Build successful"

# Check if application is already running
if pgrep -f "chitti-manager" > /dev/null; then
    echo "Stopping existing application..."
    pkill -f "chitti-manager"
    sleep 3
fi

# Start the application
echo "Starting Chitti Manager..."
nohup java -jar target/chitti-manager-0.0.1-SNAPSHOT.jar > application.log 2>&1 &

# Wait for application to start
echo "Waiting for application to start..."
sleep 15

# Check if application started successfully
if curl -s "http://localhost:8080/api/chitties" > /dev/null; then
    echo "✓ Application started successfully!"
    echo ""
    echo "🎉 Chitti Manager is now running!"
    echo ""
    echo "📍 Application URL: http://localhost:8080"
    echo "📖 API Documentation: http://localhost:8080/swagger-ui/index.html"
    echo "📋 Logs: tail -f application.log"
    echo ""
    echo "🧪 Quick Test:"
    echo "curl http://localhost:8080/api/chitties"
else
    echo "❌ Application failed to start. Check application.log for details."
    tail -n 20 application.log
    exit 1
fi