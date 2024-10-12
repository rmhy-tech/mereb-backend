# Dockerfile for running the built JAR
FROM openjdk:21-jdk-slim

WORKDIR /app

# Expose the application port
EXPOSE 8082

# Copy the JAR built by Jenkins
COPY target/*.jar user-service.jar

# Start the service
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]