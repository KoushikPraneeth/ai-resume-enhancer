package com.koushikpraneeth.resume_enhancer_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.koushikpraneeth.resume_enhancer_backend.model.ResumeRequest;
import com.koushikpraneeth.resume_enhancer_backend.model.ResumeRequest.PersonalInfo;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final WebClient webClient;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${latex.temp.dir}")
    private String latexTempDir;

    public ResumeServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.groq.com").build();
    }

    @Override
    public byte[] processResume(ResumeRequest request) {
        String[] enhancedSections = enhanceSections(request.getJobDescription(), request.getSections());
        String latexCode = generateLatex(request.getPersonalInfo(), enhancedSections);
        return compileLatexToPdf(latexCode);
    }

    private String[] enhanceSections(String jobDescription, String[] sections) {
        return Arrays.stream(sections)
                .map(section -> {
                    String prompt = "Enhance this resume section based on the job description, making it more impactful and aligned with the job requirements. Keep the response concise and professional:\n" +
                            "Job Description: " + jobDescription + "\n" +
                            "Section: " + section;
                    return webClient.post()
                            .uri("/openai/v1/chat/completions")
                            .header("Authorization", "Bearer " + groqApiKey)
                            .header("Content-Type", "application/json")
                            .bodyValue(new GroqRequest(prompt))
                            .retrieve()
                            .bodyToMono(GroqResponse.class)
                            .block()
                            .getChoices()[0].getMessage().getContent();
                })
                .toArray(String[]::new);
    }

    private String generateLatex(PersonalInfo personalInfo, String[] enhancedSections) {
        StringBuilder latex = new StringBuilder();
        latex.append("\\documentclass{resume}\n")
             .append("\\usepackage{graphicx}\n")
             .append("\\begin{document}\n")
             .append("\\name{").append(escapeLatex(personalInfo.getFullName())).append("}\n")
             .append("\\begin{resume}\n");

        for (String section : enhancedSections) {
            latex.append(section).append("\n\n");
        }

        latex.append("\\end{resume}\n")
             .append("\\end{document}");
        return latex.toString();
    }

    private String escapeLatex(String text) {
        return text.replaceAll("[&#$%_{}]", "\\\\$0");
    }

    private byte[] compileLatexToPdf(String latexCode) {
        try {
            // Create temp directory
            File tempDir = new File(latexTempDir, String.valueOf(System.currentTimeMillis()));
            tempDir.mkdirs();
            
            // Write LaTeX code to file
            File texFile = new File(tempDir, "resume.tex");
            Files.write(texFile.toPath(), latexCode.getBytes());

            // Compile LaTeX to PDF using local pdflatex installation
            ProcessBuilder pb = new ProcessBuilder("pdflatex", "-interaction=nonstopmode", texFile.getName());
            pb.directory(tempDir);
            
            // Redirect error stream to output stream
            pb.redirectErrorStream(true);
            
            // Start process and wait for completion
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                // Read the error output
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String errorOutput = reader.lines().reduce("", (a, b) -> a + "\n" + b);
                    throw new RuntimeException("LaTeX compilation failed: " + errorOutput);
                }
            }

            // Read the generated PDF
            File pdfFile = new File(tempDir, "resume.pdf");
            byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

            // Clean up
            Files.walk(tempDir.toPath())
                 .sorted((a, b) -> b.compareTo(a))
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         // Log error but continue
                         e.printStackTrace();
                     }
                 });

            return pdfBytes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to compile LaTeX to PDF", e);
        }
    }

    // Inner classes for Groq API requests/responses
    private static class GroqRequest {
        private final String model = "deepseek-r1-distill-llama-70b";
        private final Message[] messages;
        private final double temperature = 0.6;
        private final int max_completion_tokens = 4096;
        private final double top_p = 0.95;
        private final boolean stream = false;

        public GroqRequest(String prompt) {
            this.messages = new Message[]{new Message("user", prompt)};
        }

        public String getModel() { return model; }
        public Message[] getMessages() { return messages; }
        public double getTemperature() { return temperature; }
        public int getMax_completion_tokens() { return max_completion_tokens; }
        public double getTop_p() { return top_p; }
        public boolean getStream() { return stream; }
    }

    private static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
    }

    private static class GroqResponse {
        private Choice[] choices;

        public Choice[] getChoices() { return choices; }
        public void setChoices(Choice[] choices) { this.choices = choices; }
    }

    private static class Choice {
        private Message message;

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
    }
}
