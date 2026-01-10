package com.sakthivel.blockmail.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for QKD Key Manager (ETSI GS QKD 014)
 */
@Configuration
@ConfigurationProperties(prefix = "qkd.keymanager")
@Getter @Setter
public class QkdKeyManagerConfig {

    private String baseUrl = "http://localhost:8080/api/v1/keys";
    private String masterKmId = "KM_001";
    private Integer defaultKeySize = 256;
    private Integer keyPoolSize = 10;
    private Long keyLifetimeSeconds = 3600L;
    private Boolean mockMode = true;
    private String authToken;
}
