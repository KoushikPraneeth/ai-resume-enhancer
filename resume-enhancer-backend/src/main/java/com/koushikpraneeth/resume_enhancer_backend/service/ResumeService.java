package com.koushikpraneeth.resume_enhancer_backend.service;

import com.koushikpraneeth.resume_enhancer_backend.model.ResumeRequest;

public interface ResumeService {
    byte[] processResume(ResumeRequest request);
}
