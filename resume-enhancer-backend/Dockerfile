# Use a base image with Java and Tex Live
FROM texlive/texlive:latest AS texlive
FROM openjdk:17-jdk-slim

# Install necessary tools
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy Tex Live from the texlive image
COPY --from=texlive /usr/local/texlive /usr/local/texlive

# Set environment variables for Tex Live
ENV PATH="/usr/local/texlive/2023/bin/x86_64-linux:${PATH}"

# Copy the Spring Boot application
WORKDIR /app
COPY target/resume-enhancer-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
