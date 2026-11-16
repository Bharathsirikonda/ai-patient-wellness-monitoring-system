package com.patientwellness.integration.llm;

import com.patientwellness.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class LLMProviderFactory {

    private final ApplicationProperties properties;
    private final OpenAiCompatibleLLMProvider openAiProvider;
    private final DeterministicFallbackLLMProvider fallbackProvider;

    @Bean
    @Primary
    public LLMProvider primaryLlmProvider() {
        String configuredProvider = properties.llm().provider();
        if ("OPENAI".equalsIgnoreCase(configuredProvider) && openAiProvider.isAvailable()) {
            return openAiProvider;
        }
        return fallbackProvider;
    }
}
