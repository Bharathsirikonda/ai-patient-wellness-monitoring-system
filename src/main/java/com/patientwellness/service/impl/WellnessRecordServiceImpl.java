package com.patientwellness.service.impl;

import com.patientwellness.dto.request.WellnessRecordRequest;
import com.patientwellness.dto.response.WellnessRecordResponse;
import com.patientwellness.entity.Patient;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.exception.DuplicateResourceException;
import com.patientwellness.exception.ResourceNotFoundException;
import com.patientwellness.repository.PatientRepository;
import com.patientwellness.repository.WellnessRecordRepository;
import com.patientwellness.security.OwnershipValidator;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.WellnessRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WellnessRecordServiceImpl implements WellnessRecordService {

    private final WellnessRecordRepository wellnessRecordRepository;
    private final PatientRepository patientRepository;
    private final OwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public WellnessRecordResponse createRecord(Long patientId, WellnessRecordRequest request, UserPrincipal principal) {
        Patient patient = ownershipValidator.getAndValidatePatient(patientId, principal);

        if (wellnessRecordRepository.existsByPatientIdAndRecordDate(patientId, request.getRecordDate())) {
            throw new DuplicateResourceException(
                    "A wellness record already exists for patient " + patientId + " on date " + request.getRecordDate()
            );
        }

        WellnessRecord record = WellnessRecord.builder()
                .patient(patient)
                .recordDate(request.getRecordDate())
                .weight(request.getWeight())
                .sleepHours(request.getSleepHours())
                .waterIntake(request.getWaterIntake())
                .exerciseMinutes(request.getExerciseMinutes())
                .mood(request.getMood())
                .energyLevel(request.getEnergyLevel())
                .stressLevel(request.getStressLevel())
                .notes(request.getNotes())
                .build();

        WellnessRecord saved = wellnessRecordRepository.save(record);

        // Update patient's current baseline weight if recorded
        if (request.getWeight() != null) {
            patient.setWeight(request.getWeight());
            patientRepository.save(patient);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WellnessRecordResponse getRecordById(Long patientId, Long recordId, UserPrincipal principal) {
        ownershipValidator.getAndValidatePatient(patientId, principal);

        WellnessRecord record = wellnessRecordRepository.findByIdAndPatientId(recordId, patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wellness record not found with id: " + recordId + " for patient: " + patientId
                ));

        return mapToResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WellnessRecordResponse> getRecordsForPatient(
            Long patientId,
            Pageable pageable,
            UserPrincipal principal
    ) {
        ownershipValidator.getAndValidatePatient(patientId, principal);
        return wellnessRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WellnessRecordResponse> getRecordsForDateRange(
            Long patientId,
            LocalDate startDate,
            LocalDate endDate,
            UserPrincipal principal
    ) {
        ownershipValidator.getAndValidatePatient(patientId, principal);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return wellnessRecordRepository.findByPatientIdAndRecordDateBetweenOrderByRecordDateAsc(patientId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public WellnessRecordResponse updateRecord(
            Long patientId,
            Long recordId,
            WellnessRecordRequest request,
            UserPrincipal principal
    ) {
        ownershipValidator.getAndValidatePatient(patientId, principal);

        WellnessRecord record = wellnessRecordRepository.findByIdAndPatientId(recordId, patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wellness record not found with id: " + recordId + " for patient: " + patientId
                ));

        if (wellnessRecordRepository.existsByPatientIdAndRecordDateAndIdNot(patientId, request.getRecordDate(), recordId)) {
            throw new DuplicateResourceException(
                    "Another wellness record already exists for date: " + request.getRecordDate()
            );
        }

        record.setRecordDate(request.getRecordDate());
        record.setWeight(request.getWeight());
        record.setSleepHours(request.getSleepHours());
        record.setWaterIntake(request.getWaterIntake());
        record.setExerciseMinutes(request.getExerciseMinutes());
        record.setMood(request.getMood());
        record.setEnergyLevel(request.getEnergyLevel());
        record.setStressLevel(request.getStressLevel());
        record.setNotes(request.getNotes());

        WellnessRecord updated = wellnessRecordRepository.save(record);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteRecord(Long patientId, Long recordId, UserPrincipal principal) {
        ownershipValidator.getAndValidatePatient(patientId, principal);

        WellnessRecord record = wellnessRecordRepository.findByIdAndPatientId(recordId, patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wellness record not found with id: " + recordId + " for patient: " + patientId
                ));

        wellnessRecordRepository.delete(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WellnessRecordResponse> getRecentRecords(Long patientId, int limit, UserPrincipal principal) {
        ownershipValidator.getAndValidatePatient(patientId, principal);
        Pageable pageable = PageRequest.of(0, Math.min(limit, 100));
        return wellnessRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId, pageable)
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private WellnessRecordResponse mapToResponse(WellnessRecord record) {
        return WellnessRecordResponse.builder()
                .id(record.getId())
                .patientId(record.getPatient().getId())
                .recordDate(record.getRecordDate())
                .weight(record.getWeight())
                .sleepHours(record.getSleepHours())
                .waterIntake(record.getWaterIntake())
                .exerciseMinutes(record.getExerciseMinutes())
                .mood(record.getMood())
                .energyLevel(record.getEnergyLevel())
                .stressLevel(record.getStressLevel())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
