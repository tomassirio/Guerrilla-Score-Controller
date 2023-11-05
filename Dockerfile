# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-slim

# Set environment variables
ENV AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
ENV AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container at the specified working directory
COPY target/*.jar ./app.jar

# Specify the command to run your application
CMD ["java", "-jar", "app.jar"]