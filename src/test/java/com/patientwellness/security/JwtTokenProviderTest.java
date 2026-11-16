package com.patientwellness.security;

import com.patientwellness.config.ApplicationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        ApplicationProperties properties = new ApplicationProperties(
                new ApplicationProperties.JwtProperties("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970", 3600000),
                new ApplicationProperties.LlmProperties("FALLBACK", "", "https://api.openai.com/v1", "gpt-4o-mini", 1000, 2000)
        );
        tokenProvider = new JwtTokenProvider(properties);
    }

    @Test
    @DisplayName("Should generate valid token and extract claims accurately")
    void shouldGenerateAndValidateToken() {
        String token = tokenProvider.generateTokenForUser(101L, "patient.tester@example.com", "USER", "Patient Tester");

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getUsernameFromToken(token)).isEqualTo("patient.tester@example.com");
        assertThat(tokenProvider.getUserIdFromToken(token)).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should reject malformed or manipulated token")
    void shouldRejectMalformedToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidpayload.invalidSignature";
        assertThat(tokenProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("Should reject empty or null token")
    void shouldRejectEmptyToken() {
        assertThat(tokenProvider.validateToken("")).isFalse();
        assertThat(tokenProvider.validateToken(null)).isFalse();
    }
}
