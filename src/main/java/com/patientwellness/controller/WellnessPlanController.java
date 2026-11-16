package com.patientwellness.controller;

import com.patientwellness.dto.request.WellnessPlanRequest;
import com.patientwellness.dto.request.WellnessPlanStatusUpdateRequest;
import com.patientwellness.dto.response.ApiResponse;
import com.patientwellness.dto.response.WellnessPlanProgressResponse;
import com.patientwellness.dto.response.WellnessPlanResponse;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.WellnessPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Wellness Plans", description = "Endpoints for creating lifestyle guidance targets, tracking adherence, and progress")
public class WellnessPlanController {

    private final WellnessPlanService wellnessPlanService;

    @PostMapping("/api/v1/patients/{patientId}/wellness-plans")
    @Operation(summary = "Create wellness plan for patient", description = "Defines hydration, sleep, and exercise lifestyle targets.")
    public ResponseEntity<ApiResponse<WellnessPlanResponse>> createPlan(
            @PathVariable Long patientId,
            @Valid @RequestBody WellnessPlanRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessPlanResponse response = wellnessPlanService.createPlan(patientId, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Wellness plan created successfully"));
    }

    @GetMapping("/api/v1/patients/{patientId}/wellness-plans")
    @Operation(summary = "Get all wellness plans for a patient", description = "Retrieves all wellness guidance plans established for a patient.")
    public ResponseEntity<ApiResponse<List<WellnessPlanResponse>>> getPlansForPatient(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<WellnessPlanResponse> plans = wellnessPlanService.getPlansForPatient(patientId, principal);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/api/v1/wellness-plans/{planId}")
    @Operation(summary = "Get specific wellness plan", description = "Retrieves details of a wellness plan by ID.")
    public ResponseEntity<ApiResponse<WellnessPlanResponse>> getPlanById(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessPlanResponse response = wellnessPlanService.getPlanById(planId, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/api/v1/wellness-plans/{planId}")
    @Operation(summary = "Update a wellness plan", description = "Updates target metrics, date range, or title for an existing plan.")
    public ResponseEntity<ApiResponse<WellnessPlanResponse>> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody WellnessPlanRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessPlanResponse response = wellnessPlanService.updatePlan(planId, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Wellness plan updated successfully"));
    }

    @PatchMapping("/api/v1/wellness-plans/{planId}/status")
    @Operation(summary = "Update wellness plan status", description = "Transitions status between ACTIVE, COMPLETED, PAUSED, CANCELLED.")
    public ResponseEntity<ApiResponse<WellnessPlanResponse>> updatePlanStatus(
            @PathVariable Long planId,
            @Valid @RequestBody WellnessPlanStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessPlanResponse response = wellnessPlanService.updatePlanStatus(planId, request.getStatus(), principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Wellness plan status updated successfully"));
    }

    @GetMapping("/api/v1/wellness-plans/{planId}/progress")
    @Operation(summary = "Track progress against targets", description = "Calculates adherence percentage against hydration, sleep, and exercise targets.")
    public ResponseEntity<ApiResponse<WellnessPlanProgressResponse>> getPlanProgress(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessPlanProgressResponse progress = wellnessPlanService.getPlanProgress(planId, principal);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }
}
