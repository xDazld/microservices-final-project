#!/bin/bash

# DNS Filtering Service - Quick Start Script

set -e

echo "=========================================="
echo "DNS Filtering Service - Quick Start"
echo "=========================================="
echo ""

# Check prerequisites
echo "Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "Error: Docker Compose is not installed"
    exit 1
fi

if ! command -v mvn &> /dev/null && ! [ -x "./mvnw" ]; then
    echo "Error: Maven is not installed"
    exit 1
fi

echo "✓ All prerequisites met"
echo ""

# Build the application
echo "Building the application..."
if [ -x "./mvnw" ]; then
    ./mvnw clean package -DskipTests
else
    mvn clean package -DskipTests
fi
echo "✓ Application built successfully"
echo ""

# Start services with Docker Compose
echo "Starting services with Docker Compose..."
docker-compose up -d

echo ""
echo "Waiting for services to be ready..."
sleep 10

# Wait for DNS service to be healthy
echo "Checking DNS service health..."
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://localhost:8080/api/v1/admin/health > /dev/null 2>&1; then
        echo "✓ DNS service is ready!"
        break
    fi
    echo "Waiting for DNS service... ($RETRY_COUNT/$MAX_RETRIES)"
    sleep 2
    RETRY_COUNT=$((RETRY_COUNT + 1))
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "Error: DNS service failed to start"
    echo "Check logs with: docker-compose logs dns-service"
    exit 1
fi

echo ""
echo "=========================================="
echo "Services are ready!"
echo "=========================================="
echo ""
echo "Access URLs:"
echo "  DNS Service API:     http://localhost:8080/api/v1"
echo "  Health Check:        http://localhost:8080/api/v1/admin/health"
echo "  Metrics:             http://localhost:8080/metrics"
echo "  Grafana Dashboard:   http://localhost:3000 (admin/admin)"
echo "  Prometheus:          http://localhost:9090"
echo ""
echo "To view logs:"
echo "  docker-compose logs -f dns-service"
echo ""
echo "To stop services:"
echo "  docker-compose down"
echo ""
echo "Try a sample DNS query:"
echo "  ./test-api.sh"
echo ""
