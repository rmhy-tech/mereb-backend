# Build Stage
FROM openjdk:21-jdk-slim AS build

LABEL authors="Leul Tewolde"

WORKDIR /app

# Copy over project files for Maven
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Cache dependencies (Maven downloads) for faster subsequent builds
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B

# Now copy the source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Run Stage
FROM openjdk:21-jdk-slim

WORKDIR /app

# Expose the port Spring Boot will run on
EXPOSE 8082

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar user-service.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
