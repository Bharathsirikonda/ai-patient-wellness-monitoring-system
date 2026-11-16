package com.patientwellness.dto.request;

import com.patientwellness.enums.Mood;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessRecordRequest {

    @Schema(description = "Date of the logged wellness entry (YYYY-MM-DD)", example = "2026-11-15")
    @NotNull(message = "Record date is required")
    @PastOrPresent(message = "Record date cannot be in the future")
    private LocalDate recordDate;

    @Schema(description = "Body weight in kilograms", example = "65.20")
    @DecimalMin(value = "1.0", message = "Weight must be at least 1.0 kg")
    @DecimalMax(value = "500.0", message = "Weight cannot exceed 500.0 kg")
    private BigDecimal weight;

    @Schema(description = "Sleep duration in hours", example = "7.5")
    @NotNull(message = "Sleep hours are required")
    @DecimalMin(value = "0.0", message = "Sleep hours cannot be negative")
    @DecimalMax(value = "24.0", message = "Sleep hours cannot exceed 24 hours")
    private BigDecimal sleepHours;

    @Schema(description = "Total water consumed in milliliters (mL)", example = "2500")
    @NotNull(message = "Water intake is required")
    @Min(value = 0, message = "Water intake cannot be negative")
    @Max(value = 15000, message = "Water intake cannot exceed 15000 mL")
    private Integer waterIntake;

    @Schema(description = "Exercise or physical activity duration in minutes", example = "45")
    @NotNull(message = "Exercise minutes are required")
    @Min(value = 0, message = "Exercise minutes cannot be negative")
    @Max(value = 1440, message = "Exercise minutes cannot exceed 1440 minutes")
    private Integer exerciseMinutes;

    @Schema(description = "Subjective mood for the day", example = "GOOD")
    @NotNull(message = "Mood is required")
    private Mood mood;

    @Schema(description = "Subjective energy level on a 1-10 scale", example = "8")
    @NotNull(message = "Energy level is required")
    @Min(value = 1, message = "Energy level must be between 1 and 10")
    @Max(value = 10, message = "Energy level must be between 1 and 10")
    private Integer energyLevel;

    @Schema(description = "Subjective stress level on a 1-10 scale", example = "4")
    @NotNull(message = "Stress level is required")
    @Min(value = 1, message = "Stress level must be between 1 and 10")
    @Max(value = 10, message = "Stress level must be between 1 and 10")
    private Integer stressLevel;

    @Schema(description = "Optional wellness notes or qualitative observations", example = "Felt energized after morning jog.")
    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;
}
