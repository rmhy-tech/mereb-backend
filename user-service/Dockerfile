FROM openjdk:21-jdk-slim

WORKDIR /app

EXPOSE 8082

COPY target/*.jar user-service.jar

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]