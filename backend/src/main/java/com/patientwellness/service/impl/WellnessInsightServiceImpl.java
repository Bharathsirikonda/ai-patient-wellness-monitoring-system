package com.patientwellness.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientwellness.dto.response.WellnessInsightResponse;
import com.patientwellness.entity.Patient;
import com.patientwellness.entity.WellnessInsight;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.exception.ResourceNotFoundException;
import com.patientwellness.integration.llm.InsightGenerationResult;
import com.patientwellness.integration.llm.LLMProvider;
import com.patientwellness.repository.WellnessInsightRepository;
import com.patientwellness.repository.WellnessRecordRepository;
import com.patientwellness.security.OwnershipValidator;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.WellnessInsightService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WellnessInsightServiceImpl implements WellnessInsightService {

    private static final Logger log = LoggerFactory.getLogger(WellnessInsightServiceImpl.class);

    private final WellnessInsightRepository insightRepository;
    private final WellnessRecordRepository recordRepository;
    private final OwnershipValidator ownershipValidator;
    private final LLMProvider llmProvider;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public WellnessInsightResponse generateInsight(Long patientId, UserPrincipal principal) {
        Patient patient = ownershipValidator.getAndValidatePatient(patientId, principal);

        // Fetch chronological records
        List<WellnessRecord> records = recordRepository.findByPatientIdOrderByRecordDateAsc(patientId);

        InsightGenerationResult result = llmProvider.generateInsights(
                records,
                patient.getDateOfBirth(),
                patient.getGender()
        );

        WellnessInsight insight = WellnessInsight.builder()
                .patient(patient)
                .summary(result.getSummary())
                .positiveObservations(toJsonString(result.getPositiveObservations()))
                .attentionAreas(toJsonString(result.getAttentionAreas()))
                .suggestions(toJsonString(result.getSuggestions()))
                .trends(toJsonString(result.getTrends()))
                .provider(result.getProviderName())
                .disclaimer(result.getDisclaimer())
                .recordsAnalyzedCount(records.size())
                .build();

        WellnessInsight saved = insightRepository.save(insight);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WellnessInsightResponse> getInsightsForPatient(Long patientId, UserPrincipal principal) {
        ownershipValidator.getAndValidatePatient(patientId, principal);

        return insightRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WellnessInsightResponse getInsightById(Long insightId, UserPrincipal principal) {
        WellnessInsight insight = insightRepository.findById(insightId)
                .orElseThrow(() -> new ResourceNotFoundException("Wellness insight not found with id: " + insightId));

        ownershipValidator.validatePatientOwnership(principal, insight.getPatient());
        return mapToResponse(insight);
    }

    private WellnessInsightResponse mapToResponse(WellnessInsight insight) {
        return WellnessInsightResponse.builder()
                .id(insight.getId())
                .patientId(insight.getPatient().getId())
                .summary(insight.getSummary())
                .positiveObservations(parseJsonList(insight.getPositiveObservations()))
                .attentionAreas(parseJsonList(insight.getAttentionAreas()))
                .suggestions(parseJsonList(insight.getSuggestions()))
                .trends(parseJsonList(insight.getTrends()))
                .provider(insight.getProvider())
                .disclaimer(insight.getDisclaimer())
                .recordsAnalyzedCount(insight.getRecordsAnalyzedCount())
                .createdAt(insight.getCreatedAt())
                .build();
    }

    private String toJsonString(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list != null ? list : Collections.emptyList());
        } catch (Exception ex) {
            log.error("Failed to serialize string list to JSON", ex);
            return "[]";
        }
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            log.warn("Failed to parse stored JSON list, falling back to singleton list: {}", ex.getMessage());
            return List.of(json);
        }
    }
}
