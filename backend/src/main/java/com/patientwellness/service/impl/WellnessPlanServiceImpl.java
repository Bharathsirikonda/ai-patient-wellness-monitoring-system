package com.patientwellness.service.impl;

import com.patientwellness.dto.request.WellnessPlanRequest;
import com.patientwellness.dto.response.WellnessPlanProgressResponse;
import com.patientwellness.dto.response.WellnessPlanResponse;
import com.patientwellness.entity.Patient;
import com.patientwellness.entity.WellnessPlan;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.PlanStatus;
import com.patientwellness.exception.ResourceNotFoundException;
import com.patientwellness.exception.ValidationException;
import com.patientwellness.repository.WellnessPlanRepository;
import com.patientwellness.repository.WellnessRecordRepository;
import com.patientwellness.security.OwnershipValidator;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.WellnessPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WellnessPlanServiceImpl implements WellnessPlanService {

    private final WellnessPlanRepository wellnessPlanRepository;
    private final WellnessRecordRepository wellnessRecordRepository;
    private final OwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public WellnessPlanResponse createPlan(Long patientId, WellnessPlanRequest request, UserPrincipal principal) {
        Patient patient = ownershipValidator.getAndValidatePatient(patientId, principal);

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ValidationException("Plan start date cannot be after end date");
        }

        WellnessPlan plan = WellnessPlan.builder()
                .patient(patient)
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .dailyWaterTarget(request.getDailyWaterTarget())
                .dailySleepTarget(request.getDailySleepTarget())
                .weeklyExerciseTarget(request.getWeeklyExerciseTarget())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(PlanStatus.ACTIVE)
                .build();

        WellnessPlan saved = wellnessPlanRepository.save(plan);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WellnessPlanResponse getPlanById(Long planId, UserPrincipal principal) {
        WellnessPlan plan = getAndValidatePlan(planId, principal);
        return mapToResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WellnessPlanResponse> getPlansForPatient(Long patientId, UserPrincipal principal) {
        ownershipValidator.getAndValidatePatient(patientId, principal);
        return wellnessPlanRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public WellnessPlanResponse updatePlan(Long planId, WellnessPlanRequest request, UserPrincipal principal) {
        WellnessPlan plan = getAndValidatePlan(planId, principal);

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ValidationException("Plan start date cannot be after end date");
        }

        plan.setTitle(request.getTitle().trim());
        plan.setDescription(request.getDescription());
        plan.setDailyWaterTarget(request.getDailyWaterTarget());
        plan.setDailySleepTarget(request.getDailySleepTarget());
        plan.setWeeklyExerciseTarget(request.getWeeklyExerciseTarget());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());

        WellnessPlan updated = wellnessPlanRepository.save(plan);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public WellnessPlanResponse updatePlanStatus(Long planId, PlanStatus status, UserPrincipal principal) {
        WellnessPlan plan = getAndValidatePlan(planId, principal);
        plan.setStatus(status);
        WellnessPlan updated = wellnessPlanRepository.save(plan);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public WellnessPlanProgressResponse getPlanProgress(Long planId, UserPrincipal principal) {
        WellnessPlan plan = getAndValidatePlan(planId, principal);

        LocalDate today = LocalDate.now();
        long totalPlanDays = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
        long daysElapsed = Math.max(0, ChronoUnit.DAYS.between(plan.getStartDate(), today) + 1);
        daysElapsed = Math.min(daysElapsed, totalPlanDays);
        long daysRemaining = Math.max(0, ChronoUnit.DAYS.between(today, plan.getEndDate()));

        LocalDate evalEnd = today.isBefore(plan.getEndDate()) ? today : plan.getEndDate();
        List<WellnessRecord> records = (plan.getStartDate().isAfter(evalEnd))
                ? List.of()
                : wellnessRecordRepository.findByPatientIdAndRecordDateBetweenOrderByRecordDateAsc(
                        plan.getPatient().getId(),
                        plan.getStartDate(),
                        evalEnd
                );

        if (records.isEmpty()) {
            return WellnessPlanProgressResponse.builder()
                    .plan(mapToResponse(plan))
                    .totalPlanDays(totalPlanDays)
                    .daysElapsed(daysElapsed)
                    .daysRemaining(daysRemaining)
                    .adherenceScorePercentage(BigDecimal.ZERO)
                    .averageDailyWater(BigDecimal.ZERO)
                    .waterAdherencePercentage(BigDecimal.ZERO)
                    .averageDailySleep(BigDecimal.ZERO)
                    .sleepAdherencePercentage(BigDecimal.ZERO)
                    .totalExerciseMinutesLogged(0)
                    .calculatedWeeklyExerciseMinutes(BigDecimal.ZERO)
                    .exerciseAdherencePercentage(BigDecimal.ZERO)
                    .adherenceSummary("No wellness records logged during the active plan period yet.")
                    .build();
        }

        int recordCount = records.size();

        // Water calculation
        double avgWater = records.stream().mapToInt(WellnessRecord::getWaterIntake).average().orElse(0.0);
        BigDecimal avgWaterBd = BigDecimal.valueOf(avgWater).setScale(1, RoundingMode.HALF_UP);
        BigDecimal waterTargetBd = BigDecimal.valueOf(plan.getDailyWaterTarget());
        BigDecimal waterAdherence = (waterTargetBd.compareTo(BigDecimal.ZERO) > 0)
                ? avgWaterBd.multiply(BigDecimal.valueOf(100)).divide(waterTargetBd, 1, RoundingMode.HALF_UP).min(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // Sleep calculation
        BigDecimal totalSleep = records.stream().map(WellnessRecord::getSleepHours).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgSleep = totalSleep.divide(BigDecimal.valueOf(recordCount), 2, RoundingMode.HALF_UP);
        BigDecimal sleepAdherence = (plan.getDailySleepTarget().compareTo(BigDecimal.ZERO) > 0)
                ? avgSleep.multiply(BigDecimal.valueOf(100)).divide(plan.getDailySleepTarget(), 1, RoundingMode.HALF_UP).min(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // Exercise calculation
        int totalExercise = records.stream().mapToInt(WellnessRecord::getExerciseMinutes).sum();
        double weeks = Math.max(1.0, (double) daysElapsed / 7.0);
        BigDecimal weeklyExercise = BigDecimal.valueOf(totalExercise / weeks).setScale(1, RoundingMode.HALF_UP);
        BigDecimal exerciseTargetBd = BigDecimal.valueOf(plan.getWeeklyExerciseTarget());
        BigDecimal exerciseAdherence = (exerciseTargetBd.compareTo(BigDecimal.ZERO) > 0)
                ? weeklyExercise.multiply(BigDecimal.valueOf(100)).divide(exerciseTargetBd, 1, RoundingMode.HALF_UP).min(BigDecimal.valueOf(100))
                : BigDecimal.valueOf(100);

        // Overall adherence: arithmetic average of water, sleep, and exercise adherence
        BigDecimal overallScore = waterAdherence.add(sleepAdherence).add(exerciseAdherence)
                .divide(BigDecimal.valueOf(3), 1, RoundingMode.HALF_UP);

        String summary = String.format(
                "Current plan adherence score: %s%%. Water target is %s%% met, sleep target is %s%% met, and exercise target is %s%% met.",
                overallScore, waterAdherence, sleepAdherence, exerciseAdherence
        );

        return WellnessPlanProgressResponse.builder()
                .plan(mapToResponse(plan))
                .totalPlanDays(totalPlanDays)
                .daysElapsed(daysElapsed)
                .daysRemaining(daysRemaining)
                .adherenceScorePercentage(overallScore)
                .averageDailyWater(avgWaterBd)
                .waterAdherencePercentage(waterAdherence)
                .averageDailySleep(avgSleep)
                .sleepAdherencePercentage(sleepAdherence)
                .totalExerciseMinutesLogged(totalExercise)
                .calculatedWeeklyExerciseMinutes(weeklyExercise)
                .exerciseAdherencePercentage(exerciseAdherence)
                .adherenceSummary(summary)
                .build();
    }

    private WellnessPlan getAndValidatePlan(Long planId, UserPrincipal principal) {
        WellnessPlan plan = wellnessPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Wellness plan not found with id: " + planId));
        ownershipValidator.validatePatientOwnership(principal, plan.getPatient());
        return plan;
    }

    private WellnessPlanResponse mapToResponse(WellnessPlan plan) {
        return WellnessPlanResponse.builder()
                .id(plan.getId())
                .patientId(plan.getPatient().getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .dailyWaterTarget(plan.getDailyWaterTarget())
                .dailySleepTarget(plan.getDailySleepTarget())
                .weeklyExerciseTarget(plan.getWeeklyExerciseTarget())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .status(plan.getStatus())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}
