# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run with JDK
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=build /app/target/todo-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
