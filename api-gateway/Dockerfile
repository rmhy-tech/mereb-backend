FROM openjdk:17-jdk-slim

WORKDIR /app

EXPOSE 8084

COPY target/*.jar api-gateway.jar

ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]