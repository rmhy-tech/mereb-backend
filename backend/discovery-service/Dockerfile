FROM openjdk:21-jdk-slim
LABEL authors="Leul"

VOLUME /tmp
EXPOSE 8761
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} discovery-service.jar

ENTRYPOINT ["jar", "-jar", "/discovery-service.jar"]