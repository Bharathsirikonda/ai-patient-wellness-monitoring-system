package com.patientwellness.dto.request;

import com.patientwellness.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
public class PatientUpdateRequest {

    @Schema(description = "Patient's full legal name", example = "Jane Doe")
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1990-05-15")
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be a past date")
    private LocalDate dateOfBirth;

    @Schema(description = "Biological/identified gender", example = "FEMALE")
    private Gender gender;

    @Schema(description = "Height in centimeters", example = "168.50")
    @DecimalMin(value = "30.0", message = "Height must be at least 30 cm")
    @DecimalMax(value = "300.0", message = "Height cannot exceed 300 cm")
    private BigDecimal height;

    @Schema(description = "Weight in kilograms", example = "64.80")
    @DecimalMin(value = "1.0", message = "Weight must be at least 1.0 kg")
    @DecimalMax(value = "500.0", message = "Weight cannot exceed 500.0 kg")
    private BigDecimal weight;

    @Schema(description = "Whether the patient record is active", example = "true")
    private Boolean active;
}
