package com.patientwellness.service.impl;

import com.patientwellness.dto.response.WellnessSummaryResponse;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.repository.WellnessRecordRepository;
import com.patientwellness.security.OwnershipValidator;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.WellnessSummaryService;
import com.patientwellness.util.StatisticsCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WellnessSummaryServiceImpl implements WellnessSummaryService {

    private final WellnessRecordRepository wellnessRecordRepository;
    private final OwnershipValidator ownershipValidator;

    @Override
    @Transactional(readOnly = true)
    public WellnessSummaryResponse getSummary(
            Long patientId,
            LocalDate startDate,
            LocalDate endDate,
            UserPrincipal principal
    ) {
        ownershipValidator.getAndValidatePatient(patientId, principal);

        LocalDate start = (startDate != null) ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = (endDate != null) ? endDate : LocalDate.now();

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        List<WellnessRecord> records = wellnessRecordRepository
                .findByPatientIdAndRecordDateBetweenOrderByRecordDateAsc(patientId, start, end);

        return StatisticsCalculator.calculateSummary(patientId, "CUSTOM", start, end, records);
    }

    @Override
    @Transactional(readOnly = true)
    public WellnessSummaryResponse getWeeklySummary(Long patientId, UserPrincipal principal) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6); // 7-day window inclusive
        ownershipValidator.getAndValidatePatient(patientId, principal);

        List<WellnessRecord> records = wellnessRecordRepository
                .findByPatientIdAndRecordDateBetweenOrderByRecordDateAsc(patientId, start, end);

        return StatisticsCalculator.calculateSummary(patientId, "WEEKLY", start, end, records);
    }

    @Override
    @Transactional(readOnly = true)
    public WellnessSummaryResponse getMonthlySummary(Long patientId, UserPrincipal principal) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29); // 30-day window inclusive
        ownershipValidator.getAndValidatePatient(patientId, principal);

        List<WellnessRecord> records = wellnessRecordRepository
                .findByPatientIdAndRecordDateBetweenOrderByRecordDateAsc(patientId, start, end);

        return StatisticsCalculator.calculateSummary(patientId, "MONTHLY", start, end, records);
    }
}
