package com.patientwellness.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientwellness.dto.response.WellnessInsightResponse;
import com.patientwellness.entity.Patient;
import com.patientwellness.entity.WellnessInsight;
import com.patientwellness.enums.Gender;
import com.patientwellness.enums.Role;
import com.patientwellness.integration.llm.InsightGenerationResult;
import com.patientwellness.integration.llm.LLMProvider;
import com.patientwellness.repository.WellnessInsightRepository;
import com.patientwellness.repository.WellnessRecordRepository;
import com.patientwellness.security.OwnershipValidator;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.impl.WellnessInsightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WellnessInsightServiceTest {

    @Mock
    private WellnessInsightRepository insightRepository;

    @Mock
    private WellnessRecordRepository recordRepository;

    @Mock
    private OwnershipValidator ownershipValidator;

    @Mock
    private LLMProvider llmProvider;

    private WellnessInsightService insightService;
    private UserPrincipal principal;
    private Patient patient;

    @BeforeEach
    void setUp() {
        insightService = new WellnessInsightServiceImpl(
                insightRepository,
                recordRepository,
                ownershipValidator,
                llmProvider,
                new ObjectMapper()
        );

        principal = new UserPrincipal(1L, "Alex Morgan", "alex@example.com", "pass", Role.USER, true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        patient = Patient.builder()
                .fullName("Taylor Morgan")
                .dateOfBirth(LocalDate.of(1995, 4, 10))
                .gender(Gender.FEMALE)
                .build();
        patient.setId(5L);
    }

    @Test
    @DisplayName("Should generate, persist, and return structured wellness insights")
    void shouldGenerateInsight() {
        when(ownershipValidator.getAndValidatePatient(5L, principal)).thenReturn(patient);
        when(recordRepository.findByPatientIdOrderByRecordDateAsc(5L)).thenReturn(Collections.emptyList());

        InsightGenerationResult insightResult = InsightGenerationResult.builder()
                .summary("Overall balanced routine.")
                .positiveObservations(List.of("Adequate sleep."))
                .attentionAreas(List.of("Increase hydration."))
                .suggestions(List.of("Drink 2L daily."))
                .trends(List.of("Steady sleep pattern."))
                .providerName("DETERMINISTIC_FALLBACK")
                .disclaimer("Not medical advice.")
                .build();

        when(llmProvider.generateInsights(any(), any(), any())).thenReturn(insightResult);

        WellnessInsight saved = WellnessInsight.builder()
                .patient(patient)
                .summary("Overall balanced routine.")
                .positiveObservations("[\"Adequate sleep.\"]")
                .attentionAreas("[\"Increase hydration.\"]")
                .suggestions("[\"Drink 2L daily.\"]")
                .trends("[\"Steady sleep pattern.\"]")
                .provider("DETERMINISTIC_FALLBACK")
                .disclaimer("Not medical advice.")
                .recordsAnalyzedCount(0)
                .build();
        saved.setId(1001L);

        when(insightRepository.save(any(WellnessInsight.class))).thenReturn(saved);

        WellnessInsightResponse response = insightService.generateInsight(5L, principal);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1001L);
        assertThat(response.getSummary()).isEqualTo("Overall balanced routine.");
        assertThat(response.getPositiveObservations()).containsExactly("Adequate sleep.");
        verify(insightRepository).save(any(WellnessInsight.class));
    }
}
