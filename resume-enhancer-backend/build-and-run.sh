#!/bin/bash

# Build the Spring Boot application
echo "Building Spring Boot application..."
./mvnw clean package -DskipTests

# Build the Docker image
echo "Building Docker image..."
docker build -t resume-enhancer .

# Run the Docker container
echo "Running Docker container..."
docker run -d \
    -p 8080:8080 \
    -e GROQ_API_KEY=${GROQ_API_KEY} \
    --name resume-enhancer \
    resume-enhancer

echo "Application is running at http://localhost:8080"
