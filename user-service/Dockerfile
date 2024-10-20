FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

EXPOSE 8082

COPY target/*.jar user-service.jar

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]