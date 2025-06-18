# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install maven
RUN apk add --no-cache maven

# Copy Maven wrapper and project files
COPY pom.xml /app/
COPY mvnw /app/
COPY .mvn /app/.mvn

# Download dependencies (will also be cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src /app/src

# Package the application
RUN ./mvnw package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/BookRecomendation-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar" ,"app.jar"]