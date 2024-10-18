FROM openjdk:21-jdk-slim

WORKDIR /app

EXPOSE 8083

COPY --from=build /app/target/*.jar post-service.jar

ENTRYPOINT ["java", "-jar", "/app/post-service.jar"]