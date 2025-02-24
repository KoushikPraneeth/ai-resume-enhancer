package com.koushikpraneeth.resume_enhancer_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.koushikpraneeth.resume_enhancer_backend.model.ResumeRequest;
import com.koushikpraneeth.resume_enhancer_backend.model.ResumeRequest.PersonalInfo;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

@Service
public class ResumeServiceImpl implements ResumeService {
    private static final Logger logger = LoggerFactory.getLogger(ResumeServiceImpl.class);

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
                    
                    GroqRequest groqRequest = new GroqRequest(prompt);
                    logger.debug("Sending request to Groq API: {}", groqRequest);
                    
                    return webClient.post()
                            .uri("/openai/v1/chat/completions")
                            .header("Authorization", "Bearer " + groqApiKey)
                            .header("Content-Type", "application/json")
                            .bodyValue(groqRequest)
                            .retrieve()
                            .bodyToMono(GroqResponse.class)
                            .doOnNext(response -> logger.debug("Received response from Groq API: {}", response))
                            .map(this::extractEnhancedContent)
                            .onErrorResume(e -> {
                                logger.error("Error calling Groq API", e);
                                return Mono.error(new RuntimeException("Failed to enhance resume section", e));
                            })
                            .block();
                })
                .toArray(String[]::new);
    }

    private String extractEnhancedContent(GroqResponse response) {
        if (response == null) {
            throw new RuntimeException("Received null response from Groq API");
        }
        
        if (response.getChoices() == null || response.getChoices().length == 0) {
            throw new RuntimeException("No choices in Groq API response");
        }
        
        Choice choice = response.getChoices()[0];
        if (choice == null || choice.getMessage() == null) {
            throw new RuntimeException("Invalid choice structure in Groq API response");
        }
        
        String content = choice.getMessage().getContent();
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Empty content in Groq API response");
        }
        
        return content;
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
                         logger.error("Failed to delete temporary file: {}", path, e);
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

        @Override
        public String toString() {
            return "GroqRequest{model='" + model + "', messages.length=" + messages.length + 
                   ", temperature=" + temperature + ", max_completion_tokens=" + max_completion_tokens + 
                   ", top_p=" + top_p + ", stream=" + stream + "}";
        }
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

        @Override
        public String toString() {
            return "Message{role='" + role + "', content='" + 
                   (content != null ? content.substring(0, Math.min(50, content.length())) + "..." : "null") + "'}";
        }
    }

    private static class GroqResponse {
        private String id;
        private String object;
        private long created;
        private String model;
        private Choice[] choices;
        private Usage usage;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getObject() { return object; }
        public void setObject(String object) { this.object = object; }
        public long getCreated() { return created; }
        public void setCreated(long created) { this.created = created; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public Choice[] getChoices() { return choices; }
        public void setChoices(Choice[] choices) { this.choices = choices; }
        public Usage getUsage() { return usage; }
        public void setUsage(Usage usage) { this.usage = usage; }

        @Override
        public String toString() {
            return "GroqResponse{id='" + id + "', object='" + object + "', created=" + created + 
                   ", model='" + model + "', choices.length=" + (choices != null ? choices.length : 0) + 
                   ", usage=" + usage + "}";
        }
    }

    private static class Choice {
        private Message message;
        private int index;
        private String finish_reason;

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public String getFinish_reason() { return finish_reason; }
        public void setFinish_reason(String finish_reason) { this.finish_reason = finish_reason; }

        @Override
        public String toString() {
            return "Choice{message=" + message + ", index=" + index + 
                   ", finish_reason='" + finish_reason + "'}";
        }
    }

    private static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

        public int getPrompt_tokens() { return prompt_tokens; }
        public void setPrompt_tokens(int prompt_tokens) { this.prompt_tokens = prompt_tokens; }
        public int getCompletion_tokens() { return completion_tokens; }
        public void setCompletion_tokens(int completion_tokens) { this.completion_tokens = completion_tokens; }
        public int getTotal_tokens() { return total_tokens; }
        public void setTotal_tokens(int total_tokens) { this.total_tokens = total_tokens; }

        @Override
        public String toString() {
            return "Usage{prompt_tokens=" + prompt_tokens + 
                   ", completion_tokens=" + completion_tokens + 
                   ", total_tokens=" + total_tokens + "}";
        }
    }
}
