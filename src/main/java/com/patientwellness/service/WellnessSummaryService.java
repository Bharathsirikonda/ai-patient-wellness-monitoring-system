package com.patientwellness.service;

import com.patientwellness.dto.response.WellnessSummaryResponse;
import com.patientwellness.security.UserPrincipal;

import java.time.LocalDate;

public interface WellnessSummaryService {
    WellnessSummaryResponse getSummary(Long patientId, LocalDate startDate, LocalDate endDate, UserPrincipal principal);
    WellnessSummaryResponse getWeeklySummary(Long patientId, UserPrincipal principal);
    WellnessSummaryResponse getMonthlySummary(Long patientId, UserPrincipal principal);
}
