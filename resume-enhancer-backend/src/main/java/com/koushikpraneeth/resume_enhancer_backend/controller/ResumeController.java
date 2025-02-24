package com.koushikpraneeth.resume_enhancer_backend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.koushikpraneeth.resume_enhancer_backend.model.ResumeRequest;
import com.koushikpraneeth.resume_enhancer_backend.service.ResumeService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8081"}) // Allow both Vite default and alternative port
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/enhance-resume")
    public ResponseEntity<byte[]> enhanceResume(@RequestBody ResumeRequest request) {
        byte[] pdfBytes = resumeService.processResume(request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            org.springframework.http.ContentDisposition.builder("attachment")
                .filename("enhanced-resume.pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
