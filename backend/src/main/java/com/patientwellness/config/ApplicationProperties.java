package com.patientwellness.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        JwtProperties jwt,
        LlmProperties llm
) {
    public record JwtProperties(
            String secret,
            long expirationMs
    ) {}

    public record LlmProperties(
            String provider,
            String apiKey,
            String apiUrl,
            String model,
            int connectTimeoutMs,
            int readTimeoutMs
    ) {}
}
