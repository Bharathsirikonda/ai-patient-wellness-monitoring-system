package com.patientwellness.dto.response;

import com.patientwellness.enums.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessPlanResponse {
    private Long id;
    private Long patientId;
    private String title;
    private String description;
    private Integer dailyWaterTarget;
    private BigDecimal dailySleepTarget;
    private Integer weeklyExerciseTarget;
    private LocalDate startDate;
    private LocalDate endDate;
    private PlanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
