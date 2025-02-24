# AI-Powered Resume Enhancer

## Overview
A system that uses Groq AI to analyze job descriptions and enhance resumes, generating polished PDFs via LaTeX.

## Tech Stack
- **Backend**: Spring Boot, Groq AI API, LaTeX, Docker
- **Frontend**: React (optional), HTML/JS
- **Infrastructure**: Railway.app, Docker Hub

## Features
- AI-powered resume customization
- LaTeX PDF generation
- Dockerized environment
- Railway deployment

## Setup
1. Clone repository
2. Set Groq API key: `export GROQ_API_KEY=your_key`
3. Build Docker image: `docker build -t resume-enhancer .`
4. Deploy to Railway via Docker Hub

## Project Structure
/resume-enhancer
├── backend
│ ├── src
│ ├── Dockerfile
│ └── pom.xml
├── frontend (optional)
│ ├── public
│ └── src
└── project.md


---

# Backend Plan (`backend/README.md`)
```markdown
## Backend Architecture

### Components
1. **AI Integration Layer**
   - Groq API client
   - Prompt engineering
   - Response parsing

2. **LaTeX Generation**
   - Template engine
   - PDF compilation
   - File management

3. **API Endpoints**
   - POST /api/enhance
   - GET /api/download/{id}

### Implementation Steps

1. **Set Up Groq Integration**
```java
// GroqClient.java
public class GroqClient {
    private final String apiKey;
    private final RestTemplate restTemplate;

    public EnhancedResume enhanceResume(ResumeRequest request) {
        String prompt = createEnhancementPrompt(request);
        GroqRequest groqRequest = new GroqRequest(
            "mixtral-8x7b-32768", 
            prompt
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        
        return restTemplate.postForObject(
            "https://api.groq.com/v1/chat/completions",
            new HttpEntity<>(groqRequest, headers),
            EnhancedResume.class
        );
    }

    private String createEnhancementPrompt(ResumeRequest request) {
        return String.format(
            "Analyze this job description:\n%s\n\nEnhance this resume:\n%s\n" +
            "Focus on matching skills, optimizing wording, and ATS compatibility." +
            "Return structured JSON with enhanced sections.",
            request.getJobDescription(),
            request.getResumeDetails()
        );
    }
}

LaTeX Service Implementation

// LatexService.java
@Service
public class LatexService {
    public String generateLatex(EnhancedResume resume) {
        return """
            \\documentclass{article}
            \\usepackage[margin=1in]{geometry}
            \\begin{document}
            \\section*{Enhanced Skills}
            %s
            \\section*{Experience}
            %s
            \\end{document}
            """.formatted(
                formatSkills(resume.getSkills()),
                formatExperience(resume.getExperience())
            );
    }
    
    private String formatSkills(List<String> skills) {
        return skills.stream()
            .map(skill -> "\\item " + escapeLatex(skill))
            .collect(Collectors.joining("\n"));
    }
}

Docker Setup

FROM openjdk:17-jdk-slim

# Install LaTeX
RUN apt-get update && \
    apt-get install -y texlive-latex-base texlive-fonts-recommended

# Add pdflatex to PATH
ENV PATH="/usr/bin:/usr/local/texlive/2023/bin/x86_64-linux:${PATH}"

COPY target/backend-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]