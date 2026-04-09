# Stage 1 - Build
FROM maven:3.9-eclipse-temurin-21 AS builder
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Stage 2 - Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/payment-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]