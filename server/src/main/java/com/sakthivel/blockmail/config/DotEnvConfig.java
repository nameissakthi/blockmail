package com.sakthivel.blockmail.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class to load .env file for environment variables
 * This ensures .env variables are loaded before Spring Boot starts
 */
@Component
public class DotEnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            // Look for .env file in current directory
            Path envPath = Paths.get(".env");
            if (!Files.exists(envPath)) {
                System.out.println("⚠️  .env file not found. Using defaults from application.properties");
                return;
            }

            Map<String, Object> envMap = new HashMap<>();

            // Read .env file line by line
            try (BufferedReader reader = new BufferedReader(new FileReader(envPath.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip comments and empty lines
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    // Parse KEY=VALUE
                    int equalsIndex = line.indexOf('=');
                    if (equalsIndex > 0) {
                        String key = line.substring(0, equalsIndex).trim();
                        String value = line.substring(equalsIndex + 1).trim();

                        // Remove quotes if present
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }

                        envMap.put(key, value);
                        // Also set as system property
                        System.setProperty(key, value);
                    }
                }
            }

            // Add to Spring's property sources with highest priority
            applicationContext.getEnvironment()
                    .getPropertySources()
                    .addFirst(new MapPropertySource("dotenvProperties", envMap));

            System.out.println("✅ .env file loaded successfully with " + envMap.size() + " variables");

        } catch (Exception e) {
            System.out.println("⚠️  Error loading .env file: " + e.getMessage());
            System.out.println("   Using defaults from application.properties");
        }
    }
}

