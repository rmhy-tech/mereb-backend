FROM openjdk:21-jdk-slim
LABEL authors="Leul"

VOLUME /tmp

EXPOSE 8888

RUN mvn clean package

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} config-server.jar

ENTRYPOINT ["java","-jar","/config-server.jar"]