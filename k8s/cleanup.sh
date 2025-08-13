#!/bin/bash

# Crypto Spread Ranking Kubernetes Cleanup Script

set -e

echo "🧹 Starting cleanup of Crypto Spread Ranking Kubernetes resources..."

# Delete resources in reverse order
echo "📊 Removing monitoring..."
kubectl delete -f k8s/monitoring/ --ignore-not-found=true

echo "🔒 Removing security policies..."
kubectl delete -f k8s/security/ --ignore-not-found=true

echo "🌐 Removing networking..."
kubectl delete -f k8s/networking/ --ignore-not-found=true

echo "🚀 Removing application..."
kubectl delete -f k8s/app/ --ignore-not-found=true

echo "🔴 Removing Redis..."
kubectl delete -f k8s/redis/ --ignore-not-found=true

echo "💾 Removing storage..."
kubectl delete -f k8s/storage/ --ignore-not-found=true

echo "📋 Removing configurations..."
kubectl delete -f k8s/config/ --ignore-not-found=true

echo "🗑️  Removing namespace (this will delete any remaining resources)..."
kubectl delete -f k8s/namespace/ --ignore-not-found=true

echo "⏳ Waiting for namespace deletion..."
kubectl wait --for=delete namespace/crypto-spread-ranking --timeout=120s || true

echo "✅ Cleanup completed successfully!"
echo ""
echo "📊 Remaining resources (should be empty):"
kubectl get all -n crypto-spread-ranking 2>/dev/null || echo "Namespace crypto-spread-ranking not found - cleanup successful!"