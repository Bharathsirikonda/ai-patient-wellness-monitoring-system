package com.patientwellness.service;

import com.patientwellness.dto.request.PatientCreateRequest;
import com.patientwellness.dto.request.PatientUpdateRequest;
import com.patientwellness.dto.response.PatientResponse;
import com.patientwellness.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    PatientResponse createPatient(PatientCreateRequest request, UserPrincipal principal);
    PatientResponse getPatientById(Long patientId, UserPrincipal principal);
    List<PatientResponse> getPatientsForCurrentUser(UserPrincipal principal);
    Page<PatientResponse> getPatientsForCurrentUser(UserPrincipal principal, Pageable pageable);
    PatientResponse updatePatient(Long patientId, PatientUpdateRequest request, UserPrincipal principal);
    void deletePatient(Long patientId, UserPrincipal principal);
}
