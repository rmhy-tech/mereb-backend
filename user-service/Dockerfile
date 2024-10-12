# Build Stage
FROM openjdk:21-jdk-slim AS build

LABEL authors="Leul Tewolde"

WORKDIR /app

# Copy the Maven wrapper and config
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Ensure mvnw is executable and download dependencies
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Run Stage
FROM openjdk:21-jdk-slim

WORKDIR /app

# Expose the port Spring Boot runs on
EXPOSE 8082

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar user-service.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
