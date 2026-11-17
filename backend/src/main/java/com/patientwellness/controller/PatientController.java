package com.patientwellness.controller;

import com.patientwellness.dto.request.PatientCreateRequest;
import com.patientwellness.dto.request.PatientUpdateRequest;
import com.patientwellness.dto.response.ApiResponse;
import com.patientwellness.dto.response.PatientResponse;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Patients", description = "Endpoints for managing patient profiles and personal health identities")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @Operation(summary = "Create a new patient profile", description = "Creates a new patient profile associated with the authenticated user.")
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(
            @Valid @RequestBody PatientCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PatientResponse response = patientService.createPatient(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Patient profile created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all patients for authenticated user", description = "Retrieves active patient profiles owned by the logged-in user.")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<PatientResponse> patients = patientService.getPatientsForCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success(patients));
    }

    @GetMapping("/page")
    @Operation(summary = "Get paginated patients for authenticated user", description = "Retrieves a paginated list of active patient profiles.")
    public ResponseEntity<ApiResponse<Page<PatientResponse>>> getPatientsPaginated(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Page<PatientResponse> patients = patientService.getPatientsForCurrentUser(principal, pageable);
        return ResponseEntity.ok(ApiResponse.success(patients));
    }

    @GetMapping("/{patientId}")
    @Operation(summary = "Get patient by ID", description = "Retrieves details of a specific patient profile if owned by the user.")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PatientResponse response = patientService.getPatientById(patientId, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{patientId}")
    @Operation(summary = "Update patient profile", description = "Updates details of a patient profile owned by the authenticated user.")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PatientResponse response = patientService.updatePatient(patientId, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Patient profile updated successfully"));
    }

    @DeleteMapping("/{patientId}")
    @Operation(summary = "Deactivate patient profile", description = "Safely deactivates a patient profile owned by the authenticated user.")
    public ResponseEntity<ApiResponse<Void>> deletePatient(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        patientService.deletePatient(patientId, principal);
        return ResponseEntity.ok(ApiResponse.message("Patient profile deactivated successfully"));
    }
}
