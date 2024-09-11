FROM openjdk:22-jdk-slim
LABEL authors="Leul"

# Set the working directory in the container
WORKDIR /app

ARG JAR_FILE=target/*.jar

# Copy the JAR file of the Spring Boot application
COPY ${JAR_FILE} /app/user-service.jar

# Expose the port on which the Spring Boot application will run
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
