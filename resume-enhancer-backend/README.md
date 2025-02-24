# Resume Enhancer Backend

A Spring Boot application that enhances resumes using Groq AI and generates PDFs using LaTeX.

## Prerequisites

- Java 17 or higher
- Docker
- Groq API Key

## Configuration

1. Set your Groq API key as an environment variable:
```bash
export GROQ_API_KEY=your-api-key-here
```

2. Or update `application.properties`:
```properties
groq.api.key=your-api-key-here
```

## Building and Running

### Using Docker (Recommended)

1. Make the build script executable:
```bash
chmod +x build-and-run.sh
```

2. Run the build script:
```bash
./build-and-run.sh
```

This will:
- Build the Spring Boot application
- Create a Docker image with Java and TeX Live
- Run the container with the application

### Manual Build

1. Build the application:
```bash
./mvnw clean package -DskipTests
```

2. Run with Java:
```bash
java -jar target/resume-enhancer-backend-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### POST /api/enhance-resume

Enhances a resume based on a job description and generates a PDF.

Request body:
```json
{
  "jobDescription": "string",
  "personalInfo": {
    "fullName": "string",
    "jobTitle": "string",
    "location": "string",
    "mobileNumber": "string",
    "email": "string",
    "linkedIn": "string",
    "github": "string"
  },
  "sections": ["string"]
}
```

Response:
- Content-Type: application/pdf
- Content-Disposition: attachment; filename=enhanced-resume.pdf
