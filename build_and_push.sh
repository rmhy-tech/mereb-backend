#!/bin/bash

services=("user-service" "post-service" "api-gateway")

for service in "${services[@]}"
do
    echo "Building and pushing $service"
    cd ./$service
    ./mvnw clean package -DskipTests
    docker build -t leultewolde/$service:latest .
    # docker push leultewolde/$service:latest
    cd ..
done

docker-compose down
docker-compose up -d

echo "All services have been built and deployed to Docker Hub"