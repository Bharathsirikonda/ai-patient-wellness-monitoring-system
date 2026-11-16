package com.patientwellness.integration.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientwellness.config.ApplicationProperties;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiCompatibleLLMProvider implements LLMProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleLLMProvider.class);

    private final ApplicationProperties properties;
    private final PromptBuilder promptBuilder;
    private final DeterministicFallbackLLMProvider fallbackProvider;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public OpenAiCompatibleLLMProvider(
            ApplicationProperties properties,
            PromptBuilder promptBuilder,
            DeterministicFallbackLLMProvider fallbackProvider
    ) {
        this.properties = properties;
        this.promptBuilder = promptBuilder;
        this.fallbackProvider = fallbackProvider;
        this.objectMapper = new ObjectMapper();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.llm().connectTimeoutMs());
        factory.setReadTimeout(properties.llm().readTimeoutMs());

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @Override
    public InsightGenerationResult generateInsights(
            List<WellnessRecord> records,
            LocalDate dateOfBirth,
            Gender gender
    ) {
        String apiKey = properties.llm().apiKey();
        if (!StringUtils.hasText(apiKey)) {
            log.info("No LLM API key configured. Executing deterministic fallback provider.");
            return fallbackProvider.generateInsights(records, dateOfBirth, gender);
        }

        try {
            String systemPrompt = promptBuilder.buildSystemPrompt();
            String userPrompt = promptBuilder.buildUserPrompt(records, dateOfBirth, gender);

            Map<String, Object> requestPayload = Map.of(
                    "model", properties.llm().model(),
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    ),
                    "temperature", 0.3,
                    "response_format", Map.of("type", "json_object")
            );

            String responseBody = restClient.post()
                    .uri(properties.llm().apiUrl() + "/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestPayload)
                    .retrieve()
                    .body(String.class);

            return parseLlmResponse(responseBody);
        } catch (Exception ex) {
            log.warn("Remote LLM integration request failed: {}. Reverting seamlessly to deterministic fallback.", ex.getMessage());
            InsightGenerationResult fallbackResult = fallbackProvider.generateInsights(records, dateOfBirth, gender);
            fallbackResult.setProviderName("OPENAI_COMPATIBLE (Fallback invoked: " + ex.getClass().getSimpleName() + ")");
            return fallbackResult;
        }
    }

    private InsightGenerationResult parseLlmResponse(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode contentNode = root.path("choices").get(0).path("message").path("content");
        String contentText = contentNode.asText();

        JsonNode parsed = objectMapper.readTree(contentText);

        String summary = parsed.path("summary").asText("No summary provided by LLM.");
        List<String> positive = extractStringList(parsed.path("positiveObservations"));
        List<String> attention = extractStringList(parsed.path("attentionAreas"));
        List<String> suggestions = extractStringList(parsed.path("suggestions"));
        List<String> trends = extractStringList(parsed.path("trends"));

        return InsightGenerationResult.builder()
                .summary(summary)
                .positiveObservations(positive)
                .attentionAreas(attention)
                .suggestions(suggestions)
                .trends(trends)
                .providerName("OPENAI (" + properties.llm().model() + ")")
                .disclaimer(DeterministicFallbackLLMProvider.MANDATORY_DISCLAIMER)
                .build();
    }

    private List<String> extractStringList(JsonNode node) {
        List<String> result = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                result.add(item.asText());
            }
        }
        return result;
    }

    @Override
    public String getProviderName() {
        return "OPENAI_COMPATIBLE (" + properties.llm().model() + ")";
    }

    @Override
    public boolean isAvailable() {
        return StringUtils.hasText(properties.llm().apiKey());
    }
}
