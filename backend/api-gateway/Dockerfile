FROM openjdk:17-jdk-slim
LABEL authors="Leul"

VOLUME /tmp

EXPOSE 8080

RUN mvn clean package

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} api-gateway.jar

ENTRYPOINT ["java","-jar","/api-gateway.jar"]