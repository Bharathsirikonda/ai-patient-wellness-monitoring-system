package com.patientwellness.service;

import com.patientwellness.dto.request.WellnessPlanRequest;
import com.patientwellness.dto.response.WellnessPlanProgressResponse;
import com.patientwellness.dto.response.WellnessPlanResponse;
import com.patientwellness.entity.Patient;
import com.patientwellness.entity.WellnessPlan;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.PlanStatus;
import com.patientwellness.enums.Role;
import com.patientwellness.exception.ValidationException;
import com.patientwellness.repository.WellnessPlanRepository;
import com.patientwellness.repository.WellnessRecordRepository;
import com.patientwellness.security.OwnershipValidator;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.impl.WellnessPlanServiceImpl;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WellnessPlanServiceTest {

    @Mock
    private WellnessPlanRepository planRepository;

    @Mock
    private WellnessRecordRepository recordRepository;

    @Mock
    private OwnershipValidator ownershipValidator;

    private WellnessPlanService planService;
    private UserPrincipal principal;
    private Patient patient;

    @BeforeEach
    void setUp() {
        planService = new WellnessPlanServiceImpl(planRepository, recordRepository, ownershipValidator);

        principal = new UserPrincipal(1L, "Alex Morgan", "alex@example.com", "pass", Role.USER, true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        patient = Patient.builder().fullName("Taylor Morgan").build();
        patient.setId(5L);
    }

    @Test
    @DisplayName("Should create wellness plan when dates are valid")
    void shouldCreatePlan() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(30);

        WellnessPlanRequest request = WellnessPlanRequest.builder()
                .title("Hydration & Cardio")
                .description("Drink 2500ml water and exercise 150 mins weekly")
                .dailyWaterTarget(2500)
                .dailySleepTarget(new BigDecimal("8.00"))
                .weeklyExerciseTarget(150)
                .startDate(start)
                .endDate(end)
                .build();

        WellnessPlan saved = WellnessPlan.builder()
                .patient(patient)
                .title("Hydration & Cardio")
                .description("Drink 2500ml water and exercise 150 mins weekly")
                .dailyWaterTarget(2500)
                .dailySleepTarget(new BigDecimal("8.00"))
                .weeklyExerciseTarget(150)
                .startDate(start)
                .endDate(end)
                .status(PlanStatus.ACTIVE)
                .build();
        saved.setId(20L);

        when(ownershipValidator.getAndValidatePatient(5L, principal)).thenReturn(patient);
        when(planRepository.save(any(WellnessPlan.class))).thenReturn(saved);

        WellnessPlanResponse response = planService.createPlan(5L, request, principal);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getStatus()).isEqualTo(PlanStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should reject wellness plan when start date is after end date")
    void shouldRejectInvalidDates() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = LocalDate.now();

        WellnessPlanRequest request = WellnessPlanRequest.builder()
                .title("Invalid Plan")
                .dailyWaterTarget(2000)
                .dailySleepTarget(new BigDecimal("7.50"))
                .weeklyExerciseTarget(100)
                .startDate(start)
                .endDate(end)
                .build();

        when(ownershipValidator.getAndValidatePatient(5L, principal)).thenReturn(patient);

        assertThatThrownBy(() -> planService.createPlan(5L, request, principal))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("start date cannot be after end date");
    }

    @Test
    @DisplayName("Should calculate plan adherence progress")
    void shouldCalculatePlanProgress() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now().plusDays(25);

        WellnessPlan plan = WellnessPlan.builder()
                .patient(patient)
                .title("Active Plan")
                .dailyWaterTarget(2000)
                .dailySleepTarget(new BigDecimal("8.00"))
                .weeklyExerciseTarget(140)
                .startDate(start)
                .endDate(end)
                .status(PlanStatus.ACTIVE)
                .build();
        plan.setId(20L);

        when(planRepository.findById(20L)).thenReturn(Optional.of(plan));

        WellnessRecord record = WellnessRecord.builder()
                .patient(patient)
                .recordDate(start)
                .waterIntake(2000)
                .sleepHours(new BigDecimal("8.00"))
                .exerciseMinutes(30)
                .build();

        when(recordRepository.findByPatientIdAndRecordDateBetweenOrderByRecordDateAsc(any(), any(), any()))
                .thenReturn(List.of(record));

        WellnessPlanProgressResponse progress = planService.getPlanProgress(20L, principal);

        assertThat(progress).isNotNull();
        assertThat(progress.getAdherenceScorePercentage()).isGreaterThan(BigDecimal.ZERO);
        assertThat(progress.getDaysElapsed()).isGreaterThan(0);
    }
}
