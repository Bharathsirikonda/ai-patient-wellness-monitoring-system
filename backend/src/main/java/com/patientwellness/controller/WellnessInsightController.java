package com.patientwellness.controller;

import com.patientwellness.dto.response.ApiResponse;
import com.patientwellness.dto.response.WellnessInsightResponse;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.WellnessInsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "AI Insights", description = "Endpoints for generating and inspecting AI lifestyle observations and trends")
public class WellnessInsightController {

    private final WellnessInsightService wellnessInsightService;

    @PostMapping("/api/v1/patients/{patientId}/insights/generate")
    @Operation(summary = "Generate AI wellness insights", description = "Analyzes logged historical records and produces actionable lifestyle observations with strict medical disclaimers.")
    public ResponseEntity<ApiResponse<WellnessInsightResponse>> generateInsight(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessInsightResponse response = wellnessInsightService.generateInsight(patientId, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Wellness insights generated successfully"));
    }

    @GetMapping("/api/v1/patients/{patientId}/insights")
    @Operation(summary = "Get previous insights for patient", description = "Retrieves all previously synthesized insight reports for a patient.")
    public ResponseEntity<ApiResponse<List<WellnessInsightResponse>>> getInsightsForPatient(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<WellnessInsightResponse> list = wellnessInsightService.getInsightsForPatient(patientId, principal);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/api/v1/insights/{insightId}")
    @Operation(summary = "Get specific insight report", description = "Retrieves a previously generated insight report by its ID.")
    public ResponseEntity<ApiResponse<WellnessInsightResponse>> getInsightById(
            @PathVariable Long insightId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessInsightResponse response = wellnessInsightService.getInsightById(insightId, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
