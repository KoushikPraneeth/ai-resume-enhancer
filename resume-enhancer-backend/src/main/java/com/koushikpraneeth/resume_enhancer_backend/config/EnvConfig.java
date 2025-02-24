package com.koushikpraneeth.resume_enhancer_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:.env")
public class EnvConfig {
}
