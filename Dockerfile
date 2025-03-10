# Use an official OpenJDK runtime as a base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the local Maven repository into the container
COPY . /app

# Install Maven
RUN apk add --no-cache maven

# Build the app (this assumes you have a `pom.xml` file)
RUN mvn clean install -DskipTests

# Command to run the app (this will depend on your project setup)
CMD ["java", "-jar", "target/chat-app-api.jar"]
