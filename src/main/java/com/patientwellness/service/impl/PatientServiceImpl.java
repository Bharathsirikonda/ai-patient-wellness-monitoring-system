package com.patientwellness.service.impl;

import com.patientwellness.dto.request.PatientCreateRequest;
import com.patientwellness.dto.request.PatientUpdateRequest;
import com.patientwellness.dto.response.PatientResponse;
import com.patientwellness.entity.Patient;
import com.patientwellness.entity.User;
import com.patientwellness.exception.ResourceNotFoundException;
import com.patientwellness.repository.PatientRepository;
import com.patientwellness.repository.UserRepository;
import com.patientwellness.security.OwnershipValidator;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final OwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public PatientResponse createPatient(PatientCreateRequest request, UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + principal.getId()));

        Patient patient = Patient.builder()
                .user(user)
                .fullName(request.getFullName().trim())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .height(request.getHeight())
                .weight(request.getWeight())
                .active(true)
                .build();

        Patient saved = patientRepository.save(patient);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long patientId, UserPrincipal principal) {
        Patient patient = ownershipValidator.getAndValidatePatient(patientId, principal);
        return mapToResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsForCurrentUser(UserPrincipal principal) {
        List<Patient> patients = patientRepository.findByUserIdAndActiveTrue(principal.getId());
        return patients.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> getPatientsForCurrentUser(UserPrincipal principal, Pageable pageable) {
        return patientRepository.findByUserIdAndActiveTrue(principal.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(Long patientId, PatientUpdateRequest request, UserPrincipal principal) {
        Patient patient = ownershipValidator.getAndValidatePatient(patientId, principal);

        patient.setFullName(request.getFullName().trim());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setHeight(request.getHeight());
        patient.setWeight(request.getWeight());
        if (request.getActive() != null) {
            patient.setActive(request.getActive());
        }

        Patient updated = patientRepository.save(patient);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deletePatient(Long patientId, UserPrincipal principal) {
        Patient patient = ownershipValidator.getAndValidatePatient(patientId, principal);
        // Soft-deactivate to preserve clinical historical integrity
        patient.setActive(false);
        patientRepository.save(patient);
    }

    private PatientResponse mapToResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .userId(patient.getUser().getId())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .height(patient.getHeight())
                .weight(patient.getWeight())
                .active(patient.isActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
