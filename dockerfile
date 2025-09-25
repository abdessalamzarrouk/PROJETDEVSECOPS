# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-jammy

# Add Maintainer info (optional)
LABEL maintainer="your-email@example.com"

# The application's jar file
ARG JAR_FILE=target/*.jar

# Copy the jar file into the container
COPY ${JAR_FILE} app.jar

# Expose the port your Spring Boot app listens on (default 8080)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]
