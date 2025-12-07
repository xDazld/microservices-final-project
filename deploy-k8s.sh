#!/bin/bash

# Kubernetes Deployment Script

set -e

echo "=========================================="
echo "DNS Service - Kubernetes Deployment"
echo "=========================================="
echo ""

# Check prerequisites
echo "Checking prerequisites..."

if ! command -v kubectl &> /dev/null; then
    echo "Error: kubectl is not installed"
    exit 1
fi

if ! kubectl cluster-info &> /dev/null; then
    echo "Error: Cannot connect to Kubernetes cluster"
    exit 1
fi

echo "✓ Connected to Kubernetes cluster"
echo ""

# Build container image
echo "Building container image..."
if [ -x "./mvnw" ]; then
    ./mvnw package -Dquarkus.container-image.build=true
else
    mvn package -Dquarkus.container-image.build=true
fi
echo "✓ Container image built"
echo ""

# Apply Kubernetes configurations
echo "Deploying to Kubernetes..."
kubectl apply -f kubernetes/deployment.yml
kubectl apply -f kubernetes/monitoring.yml
echo "✓ Configurations applied"
echo ""

# Wait for deployments
echo "Waiting for deployments to be ready..."
kubectl wait --for=condition=available --timeout=300s \
  deployment/dns-service-deployment -n dns-service
echo "✓ DNS service deployment ready"
echo ""

# Get service information
echo "=========================================="
echo "Deployment Information"
echo "=========================================="
echo ""
echo "Pods:"
kubectl get pods -n dns-service
echo ""
echo "Services:"
kubectl get services -n dns-service
echo ""
echo "HPA Status:"
kubectl get hpa -n dns-service
echo ""

# Get service URL
SERVICE_IP=$(kubectl get service dns-service -n dns-service -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
if [ -z "$SERVICE_IP" ]; then
    SERVICE_IP=$(kubectl get service dns-service -n dns-service -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
fi

if [ -z "$SERVICE_IP" ]; then
    echo "Service is not yet exposed. Use port-forward to access:"
    echo "  kubectl port-forward -n dns-service service/dns-service 8080:8080"
else
    echo "Service URL: http://$SERVICE_IP:8080"
fi
echo ""

echo "Grafana Dashboard:"
GRAFANA_IP=$(kubectl get service grafana-service -n dns-service -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
if [ -z "$GRAFANA_IP" ]; then
    echo "  kubectl port-forward -n dns-service service/grafana-service 3000:3000"
else
    echo "  http://$GRAFANA_IP:3000 (admin/admin)"
fi
echo ""

echo "To view logs:"
echo "  kubectl logs -n dns-service -l app=dns-filtering-service -f"
echo ""
echo "To scale:"
echo "  kubectl scale deployment dns-service-deployment --replicas=5 -n dns-service"
echo ""
echo "To delete:"
echo "  kubectl delete namespace dns-service"
echo ""
