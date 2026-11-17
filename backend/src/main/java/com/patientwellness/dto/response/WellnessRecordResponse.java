package com.patientwellness.dto.response;

import com.patientwellness.enums.Mood;
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
public class WellnessRecordResponse {
    private Long id;
    private Long patientId;
    private LocalDate recordDate;
    private BigDecimal weight;
    private BigDecimal sleepHours;
    private Integer waterIntake;
    private Integer exerciseMinutes;
    private Mood mood;
    private Integer energyLevel;
    private Integer stressLevel;
    private String notes;
    private LocalDateTime createdAt;
}
