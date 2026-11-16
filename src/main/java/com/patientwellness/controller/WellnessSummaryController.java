package com.patientwellness.controller;

import com.patientwellness.dto.response.ApiResponse;
import com.patientwellness.dto.response.WellnessSummaryResponse;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.WellnessSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/wellness-summary")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Wellness Summary", description = "Endpoints for statistical health summaries, averages, and trend evaluations")
public class WellnessSummaryController {

    private final WellnessSummaryService wellnessSummaryService;

    @GetMapping
    @Operation(summary = "Get custom date range wellness summary", description = "Calculates average sleep, water, activity, weight changes, and mood distributions over a range.")
    public ResponseEntity<ApiResponse<WellnessSummaryResponse>> getCustomSummary(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessSummaryResponse summary = wellnessSummaryService.getSummary(patientId, startDate, endDate, principal);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/weekly")
    @Operation(summary = "Get trailing 7-day weekly wellness summary", description = "Calculates summary metrics for the past 7 days.")
    public ResponseEntity<ApiResponse<WellnessSummaryResponse>> getWeeklySummary(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessSummaryResponse summary = wellnessSummaryService.getWeeklySummary(patientId, principal);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get trailing 30-day monthly wellness summary", description = "Calculates summary metrics for the past 30 days.")
    public ResponseEntity<ApiResponse<WellnessSummaryResponse>> getMonthlySummary(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessSummaryResponse summary = wellnessSummaryService.getMonthlySummary(patientId, principal);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
