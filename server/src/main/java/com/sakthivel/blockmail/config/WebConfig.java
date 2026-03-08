package com.sakthivel.blockmail.config;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${client.urls}")
    private String frontendUrls;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // Split comma-separated URLs
        String[] allowedOrigins = frontendUrls.split(",");

        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Allow all origins including Electron (file://)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type", "X-Total-Count")
                .allowCredentials(true)
                .maxAge(3600);
    }
}