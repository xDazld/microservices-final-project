#!/bin/bash

# DNS Filtering Service - API Testing Script

BASE_URL="http://localhost:8080/api/v1"

echo "=========================================="
echo "DNS Filtering Service - API Tests"
echo "=========================================="
echo ""

# Test 1: Health Check
echo "1. Testing Health Check..."
curl -s $BASE_URL/admin/health | jq '.'
echo ""

# Test 2: DNS Resolution
echo "2. Testing DNS Resolution..."
curl -s -X POST $BASE_URL/dns/resolve \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "google.com",
    "queryType": "A",
    "clientIp": "192.168.1.100"
  }' | jq '.'
echo ""

# Test 3: Batch DNS Resolution
echo "3. Testing Batch DNS Resolution..."
curl -s -X POST $BASE_URL/dns/resolve/batch \
  -H "Content-Type: application/json" \
  -d '{
    "domains": ["google.com", "github.com", "stackoverflow.com"],
    "queryType": "A"
  }' | jq '.'
echo ""

# Test 4: Get Filter Rules
echo "4. Getting Filter Rules..."
curl -s $BASE_URL/filters | jq '.[:3]'
echo ""

# Test 5: Get Filter Statistics
echo "5. Getting Filter Statistics..."
curl -s $BASE_URL/filters/stats | jq '.'
echo ""

# Test 6: Get Overall Statistics
echo "6. Getting Overall Statistics..."
curl -s $BASE_URL/admin/stats | jq '.'
echo ""

# Test 7: Test Blocked Domain
echo "7. Testing Blocked Domain (doubleclick.net)..."
curl -s -X POST $BASE_URL/dns/resolve \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "ads.doubleclick.net",
    "queryType": "A"
  }' | jq '.'
echo ""

# Test 8: Analyze Domain for Threats
echo "8. Analyzing Domain for Security Threats..."
curl -s -X POST $BASE_URL/admin/security/analyze \
  -H "Content-Type: application/json" \
  -d '{"domain": "suspicious-domain-with-many-numbers-12345.com"}' | jq '.'
echo ""

# Test 9: Cache Statistics
echo "9. Getting Cache Statistics..."
curl -s $BASE_URL/admin/cache/stats | jq '.'
echo ""

# Test 10: Security Statistics
echo "10. Getting Security Statistics..."
curl -s $BASE_URL/admin/security/stats | jq '.'
echo ""

echo "=========================================="
echo "API Tests Complete!"
echo "=========================================="
echo ""
echo "Performance test example:"
echo "  ab -n 1000 -c 10 -p query.json -T application/json $BASE_URL/dns/resolve"
echo ""
echo "Where query.json contains:"
echo '  {"domain": "example.com", "queryType": "A"}'
echo ""
