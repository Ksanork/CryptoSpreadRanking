#!/bin/bash

# Crypto Spread Ranking Kubernetes Deployment Script

set -e

echo "🚀 Starting Kubernetes deployment for Crypto Spread Ranking..."

# Build Docker image
echo "📦 Building Docker image..."
docker build -t crypto-spread-ranking:latest .

# Apply Kubernetes manifests in order
echo "🔧 Creating namespace..."
kubectl apply -f k8s/namespace/

echo "📋 Applying configurations..."
kubectl apply -f k8s/config/

echo "💾 Setting up storage..."
kubectl apply -f k8s/storage/

echo "🔴 Deploying Redis..."
kubectl apply -f k8s/redis/

echo "⏳ Waiting for Redis to be ready..."
kubectl wait --for=condition=ready pod -l app=redis -n crypto-spread-ranking --timeout=300s

echo "🚀 Deploying application..."
kubectl apply -f k8s/app/

echo "🌐 Setting up networking..."
kubectl apply -f k8s/networking/

echo "🔒 Applying security policies..."
kubectl apply -f k8s/security/

echo "📊 Setting up monitoring..."
kubectl apply -f k8s/monitoring/

echo "⏳ Waiting for application to be ready..."
kubectl wait --for=condition=available deployment/crypto-spread-ranking -n crypto-spread-ranking --timeout=300s

echo "✅ Deployment completed successfully!"
echo ""
echo "📊 Current status:"
kubectl get pods -n crypto-spread-ranking
echo ""
echo "🌐 Service endpoints:"
kubectl get services -n crypto-spread-ranking
echo ""
echo "🎯 Access the application:"
echo "  - LoadBalancer: kubectl get svc crypto-spread-ranking-service -n crypto-spread-ranking"
echo "  - Port Forward: kubectl port-forward svc/crypto-spread-ranking-service 8080:80 -n crypto-spread-ranking"
echo "  - Test endpoint: curl -H 'Authorization: Bearer ABC123' http://localhost:8080/api/spread/ranking"