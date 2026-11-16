package com.patientwellness.dto.request;

import com.patientwellness.enums.PlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessPlanStatusUpdateRequest {

    @Schema(description = "Updated status of the wellness plan", example = "COMPLETED")
    @NotNull(message = "Status is required")
    private PlanStatus status;
}
