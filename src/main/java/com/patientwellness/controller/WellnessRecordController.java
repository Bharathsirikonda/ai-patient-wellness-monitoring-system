package com.patientwellness.controller;

import com.patientwellness.dto.request.WellnessRecordRequest;
import com.patientwellness.dto.response.ApiResponse;
import com.patientwellness.dto.response.WellnessRecordResponse;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.WellnessRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/wellness-records")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Wellness Records", description = "Endpoints for recording and monitoring daily wellness metrics")
public class WellnessRecordController {

    private final WellnessRecordService wellnessRecordService;

    @PostMapping
    @Operation(summary = "Log a daily wellness record", description = "Records daily health metrics (sleep, water, activity, mood, stress) for a patient.")
    public ResponseEntity<ApiResponse<WellnessRecordResponse>> createRecord(
            @PathVariable Long patientId,
            @Valid @RequestBody WellnessRecordRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessRecordResponse response = wellnessRecordService.createRecord(patientId, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Wellness record created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get wellness records for a patient", description = "Retrieves paginated wellness records, or filters by date range if provided.")
    public ResponseEntity<ApiResponse<?>> getRecords(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, sort = "recordDate") Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (startDate != null && endDate != null) {
            List<WellnessRecordResponse> list = wellnessRecordService.getRecordsForDateRange(patientId, startDate, endDate, principal);
            return ResponseEntity.ok(ApiResponse.success(list));
        }

        Page<WellnessRecordResponse> page = wellnessRecordService.getRecordsForPatient(patientId, pageable, principal);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent wellness history", description = "Retrieves the most recent N records for the patient (default 7).")
    public ResponseEntity<ApiResponse<List<WellnessRecordResponse>>> getRecentRecords(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "7") int limit,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<WellnessRecordResponse> recent = wellnessRecordService.getRecentRecords(patientId, limit, principal);
        return ResponseEntity.ok(ApiResponse.success(recent));
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "Get specific wellness record", description = "Retrieves a specific wellness log by record ID.")
    public ResponseEntity<ApiResponse<WellnessRecordResponse>> getRecordById(
            @PathVariable Long patientId,
            @PathVariable Long recordId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessRecordResponse response = wellnessRecordService.getRecordById(patientId, recordId, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{recordId}")
    @Operation(summary = "Update an existing wellness record", description = "Updates daily wellness markers for an existing record.")
    public ResponseEntity<ApiResponse<WellnessRecordResponse>> updateRecord(
            @PathVariable Long patientId,
            @PathVariable Long recordId,
            @Valid @RequestBody WellnessRecordRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WellnessRecordResponse response = wellnessRecordService.updateRecord(patientId, recordId, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Wellness record updated successfully"));
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "Delete a wellness record", description = "Deletes a specific wellness log.")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(
            @PathVariable Long patientId,
            @PathVariable Long recordId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        wellnessRecordService.deleteRecord(patientId, recordId, principal);
        return ResponseEntity.ok(ApiResponse.message("Wellness record deleted successfully"));
    }
}
