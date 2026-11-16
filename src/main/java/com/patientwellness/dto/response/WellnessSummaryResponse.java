package com.patientwellness.dto.response;

import com.patientwellness.enums.Mood;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessSummaryResponse {
    private Long patientId;
    private String period; // "WEEKLY", "MONTHLY", "CUSTOM"
    private LocalDate startDate;
    private LocalDate endDate;
    private long totalDaysInPeriod;
    private long totalRecordsLogged;
    private BigDecimal completionRatePercentage;
    private BigDecimal averageSleepHours;
    private BigDecimal averageWaterIntakeMl;
    private BigDecimal averageExerciseMinutesPerDay;
    private Integer totalExerciseMinutes;
    private BigDecimal startWeight;
    private BigDecimal latestWeight;
    private BigDecimal weightChangeKg;
    private BigDecimal averageEnergyLevel;
    private BigDecimal averageStressLevel;
    private Map<Mood, Long> moodDistribution;
    private String summaryObservation;
}
