package com.patientwellness.security;

import com.patientwellness.entity.Patient;
import com.patientwellness.enums.Role;
import com.patientwellness.exception.ForbiddenException;
import com.patientwellness.exception.ResourceNotFoundException;
import com.patientwellness.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnershipValidator {

    private final PatientRepository patientRepository;

    /**
     * Validates that the authenticated user owns the given patient entity.
     * Administrators can access resources, but non-admins strictly cannot cross tenant boundaries.
     */
    public void validatePatientOwnership(UserPrincipal principal, Patient patient) {
        if (principal == null) {
            throw new ForbiddenException("Authentication required to access patient resources");
        }

        if (principal.getRole() == Role.ADMIN) {
            return;
        }

        if (!patient.getUser().getId().equals(principal.getId())) {
            throw new ForbiddenException("Access denied: You do not own this patient profile");
        }
    }

    /**
     * Retrieves the patient and verifies ownership in a single step.
     */
    public Patient getAndValidatePatient(Long patientId, UserPrincipal principal) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        validatePatientOwnership(principal, patient);
        return patient;
    }
}
