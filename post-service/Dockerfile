FROM openjdk:21-jdk-slim AS build

LABEL authors="Leul Tewolde"

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

COPY src ./src

RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

EXPOSE 8083

COPY --from=build /app/target/*.jar post-service.jar

ENTRYPOINT ["java", "-jar", "/app/post-service.jar"]