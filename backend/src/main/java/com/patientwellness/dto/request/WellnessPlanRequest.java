package com.patientwellness.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class WellnessPlanRequest {

    @Schema(description = "Plan title", example = "Cardio & Hydration Routine")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @Schema(description = "Plan description", example = "Focus on reaching 2.5L water daily and 180 min moderate cardio weekly")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Schema(description = "Target daily water intake in mL", example = "2500")
    @NotNull(message = "Daily water target is required")
    @Min(value = 500, message = "Daily water target must be at least 500 mL")
    @Max(value = 10000, message = "Daily water target cannot exceed 10000 mL")
    private Integer dailyWaterTarget;

    @Schema(description = "Target daily sleep in hours", example = "8.0")
    @NotNull(message = "Daily sleep target is required")
    @DecimalMin(value = "4.0", message = "Daily sleep target must be at least 4.0 hours")
    @DecimalMax(value = "14.0", message = "Daily sleep target cannot exceed 14.0 hours")
    private BigDecimal dailySleepTarget;

    @Schema(description = "Target weekly exercise duration in minutes", example = "180")
    @NotNull(message = "Weekly exercise target is required")
    @Min(value = 0, message = "Weekly exercise target cannot be negative")
    @Max(value = 3000, message = "Weekly exercise target cannot exceed 3000 minutes")
    private Integer weeklyExerciseTarget;

    @Schema(description = "Plan start date (YYYY-MM-DD)", example = "2026-11-01")
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Schema(description = "Plan end date (YYYY-MM-DD)", example = "2026-11-30")
    @NotNull(message = "End date is required")
    private LocalDate endDate;
}
