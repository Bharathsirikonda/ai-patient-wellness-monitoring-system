package com.patientwellness.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessPlanProgressResponse {
    private WellnessPlanResponse plan;
    private long totalPlanDays;
    private long daysElapsed;
    private long daysRemaining;
    private BigDecimal adherenceScorePercentage;
    private BigDecimal averageDailyWater;
    private BigDecimal waterAdherencePercentage;
    private BigDecimal averageDailySleep;
    private BigDecimal sleepAdherencePercentage;
    private Integer totalExerciseMinutesLogged;
    private BigDecimal calculatedWeeklyExerciseMinutes;
    private BigDecimal exerciseAdherencePercentage;
    private String adherenceSummary;
}
