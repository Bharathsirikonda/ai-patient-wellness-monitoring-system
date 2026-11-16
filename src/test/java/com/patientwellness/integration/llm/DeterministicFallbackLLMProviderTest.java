package com.patientwellness.integration.llm;

import com.patientwellness.entity.Patient;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.Gender;
import com.patientwellness.enums.Mood;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeterministicFallbackLLMProviderTest {

    private DeterministicFallbackLLMProvider fallbackProvider;

    @BeforeEach
    void setUp() {
        fallbackProvider = new DeterministicFallbackLLMProvider();
    }

    @Test
    @DisplayName("Should generate structured insights with medical disclaimers from wellness logs")
    void shouldGenerateStructuredInsights() {
        Patient patient = Patient.builder().build();
        patient.setId(1L);

        WellnessRecord r1 = WellnessRecord.builder()
                .patient(patient)
                .recordDate(LocalDate.now().minusDays(1))
                .sleepHours(new BigDecimal("7.50"))
                .waterIntake(2400)
                .exerciseMinutes(45)
                .mood(Mood.EXCELLENT)
                .energyLevel(8)
                .stressLevel(3)
                .build();

        InsightGenerationResult result = fallbackProvider.generateInsights(
                List.of(r1),
                LocalDate.of(1995, 3, 10),
                Gender.FEMALE
        );

        assertThat(result).isNotNull();
        assertThat(result.getProviderName()).contains("DETERMINISTIC_FALLBACK");
        assertThat(result.getDisclaimer()).contains("do not constitute medical diagnosis");
        assertThat(result.getSummary()).isNotBlank();
        assertThat(result.getPositiveObservations()).isNotEmpty();
        assertThat(result.getSuggestions()).isNotEmpty();
        assertThat(result.getTrends()).isNotEmpty();
    }

    @Test
    @DisplayName("Should provide informational guidance when record set is empty")
    void shouldHandleEmptyRecordList() {
        InsightGenerationResult result = fallbackProvider.generateInsights(
                List.of(),
                LocalDate.of(1990, 1, 1),
                Gender.OTHER
        );

        assertThat(result).isNotNull();
        assertThat(result.getSummary()).contains("Insufficient historical wellness records");
        assertThat(result.getDisclaimer()).isNotBlank();
    }
}
