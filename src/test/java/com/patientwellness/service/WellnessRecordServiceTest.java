package com.patientwellness.service;

import com.patientwellness.dto.request.WellnessRecordRequest;
import com.patientwellness.dto.response.WellnessRecordResponse;
import com.patientwellness.entity.Patient;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.Mood;
import com.patientwellness.enums.Role;
import com.patientwellness.exception.DuplicateResourceException;
import com.patientwellness.repository.PatientRepository;
import com.patientwellness.repository.WellnessRecordRepository;
import com.patientwellness.security.OwnershipValidator;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.impl.WellnessRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WellnessRecordServiceTest {

    @Mock
    private WellnessRecordRepository recordRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private OwnershipValidator ownershipValidator;

    private WellnessRecordService recordService;
    private UserPrincipal principal;
    private Patient patient;

    @BeforeEach
    void setUp() {
        recordService = new WellnessRecordServiceImpl(recordRepository, patientRepository, ownershipValidator);

        principal = new UserPrincipal(1L, "Alex Morgan", "alex@example.com", "pass", Role.USER, true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        patient = Patient.builder().fullName("Taylor Morgan").build();
        patient.setId(5L);
    }

    @Test
    @DisplayName("Should create wellness record and update patient weight")
    void shouldCreateRecord() {
        LocalDate today = LocalDate.now();
        WellnessRecordRequest request = WellnessRecordRequest.builder()
                .recordDate(today)
                .sleepHours(new BigDecimal("8.00"))
                .waterIntake(2500)
                .exerciseMinutes(45)
                .weight(new BigDecimal("68.50"))
                .energyLevel(8)
                .stressLevel(3)
                .mood(Mood.GOOD)
                .notes("Feeling energetic")
                .build();

        when(ownershipValidator.getAndValidatePatient(5L, principal)).thenReturn(patient);
        when(recordRepository.existsByPatientIdAndRecordDate(5L, today)).thenReturn(false);

        WellnessRecord saved = WellnessRecord.builder()
                .patient(patient)
                .recordDate(today)
                .sleepHours(new BigDecimal("8.00"))
                .waterIntake(2500)
                .exerciseMinutes(45)
                .weight(new BigDecimal("68.50"))
                .energyLevel(8)
                .stressLevel(3)
                .mood(Mood.GOOD)
                .notes("Feeling energetic")
                .build();
        saved.setId(100L);

        when(recordRepository.save(any(WellnessRecord.class))).thenReturn(saved);

        WellnessRecordResponse response = recordService.createRecord(5L, request, principal);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getWaterIntake()).isEqualTo(2500);
        verify(patientRepository).save(patient);
    }

    @Test
    @DisplayName("Should reject duplicate wellness record for same patient and date")
    void shouldRejectDuplicateDateRecord() {
        LocalDate today = LocalDate.now();
        WellnessRecordRequest request = WellnessRecordRequest.builder()
                .recordDate(today)
                .build();

        when(ownershipValidator.getAndValidatePatient(5L, principal)).thenReturn(patient);
        when(recordRepository.existsByPatientIdAndRecordDate(5L, today)).thenReturn(true);

        assertThatThrownBy(() -> recordService.createRecord(5L, request, principal))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists for patient");
    }
}
