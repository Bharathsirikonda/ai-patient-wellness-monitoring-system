package com.patientwellness.util;

import com.patientwellness.dto.response.WellnessSummaryResponse;
import com.patientwellness.entity.Patient;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.Mood;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsCalculatorTest {

    @Test
    @DisplayName("Should accurately compute wellness statistical metrics for a series of records")
    void shouldComputeCorrectStatistics() {
        Patient patient = Patient.builder().build();
        patient.setId(1L);

        LocalDate d1 = LocalDate.of(2026, 11, 1);
        LocalDate d2 = LocalDate.of(2026, 11, 2);

        WellnessRecord r1 = WellnessRecord.builder()
                .patient(patient)
                .recordDate(d1)
                .sleepHours(new BigDecimal("8.00"))
                .waterIntake(2500)
                .exerciseMinutes(40)
                .weight(new BigDecimal("70.00"))
                .energyLevel(8)
                .stressLevel(3)
                .mood(Mood.EXCELLENT)
                .build();

        WellnessRecord r2 = WellnessRecord.builder()
                .patient(patient)
                .recordDate(d2)
                .sleepHours(new BigDecimal("6.00"))
                .waterIntake(1500)
                .exerciseMinutes(20)
                .weight(new BigDecimal("69.50"))
                .energyLevel(6)
                .stressLevel(5)
                .mood(Mood.GOOD)
                .build();

        WellnessSummaryResponse summary = StatisticsCalculator.calculateSummary(
                1L,
                "CUSTOM",
                d1,
                d2,
                List.of(r1, r2)
        );

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalRecordsLogged()).isEqualTo(2);
        assertThat(summary.getAverageSleepHours()).isEqualByComparingTo("7.00");
        assertThat(summary.getAverageWaterIntakeMl()).isEqualByComparingTo("2000.0");
        assertThat(summary.getAverageExerciseMinutesPerDay()).isEqualByComparingTo("30.0");
        assertThat(summary.getTotalExerciseMinutes()).isEqualTo(60);
        assertThat(summary.getStartWeight()).isEqualByComparingTo("70.00");
        assertThat(summary.getLatestWeight()).isEqualByComparingTo("69.50");
        assertThat(summary.getWeightChangeKg()).isEqualByComparingTo("-0.50");
        assertThat(summary.getAverageEnergyLevel()).isEqualByComparingTo("7.0");
        assertThat(summary.getAverageStressLevel()).isEqualByComparingTo("4.0");
        assertThat(summary.getMoodDistribution()).containsEntry(Mood.EXCELLENT, 1L);
        assertThat(summary.getMoodDistribution()).containsEntry(Mood.GOOD, 1L);
    }

    @Test
    @DisplayName("Should handle empty record set gracefully without division by zero")
    void shouldHandleEmptyRecords() {
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();

        WellnessSummaryResponse summary = StatisticsCalculator.calculateSummary(
                1L,
                "WEEKLY",
                start,
                end,
                List.of()
        );

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalRecordsLogged()).isZero();
        assertThat(summary.getCompletionRatePercentage()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getAverageSleepHours()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getSummaryObservation()).contains("No wellness records found");
    }
}
