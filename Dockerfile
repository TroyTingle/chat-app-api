# Step 1: Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy only necessary files to leverage Docker cache
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Step 2: Runtime Stage
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy only the built JAR from the previous stage
COPY --from=build /app/target/chat-app-api.jar /app/chat-app-api.jar

# Command to run the application
CMD ["java", "-jar", "/app/chat-app-api.jar"]
