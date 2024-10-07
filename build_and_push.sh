#!/bin/bash

services=("user-service" "post-service" "mereb-web")

for service in "${services[@]}"
do
    echo "Building and pushing $service"
    cd ./$service
    docker build -t leultewolde/$service:latest .
    docker push leultewolde/$service:latest
    cd ..
done

echo "All services have been built and pushed to Docker Hub"