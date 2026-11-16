package com.patientwellness.service;

import com.patientwellness.dto.response.WellnessInsightResponse;
import com.patientwellness.security.UserPrincipal;

import java.util.List;

public interface WellnessInsightService {
    WellnessInsightResponse generateInsight(Long patientId, UserPrincipal principal);
    List<WellnessInsightResponse> getInsightsForPatient(Long patientId, UserPrincipal principal);
    WellnessInsightResponse getInsightById(Long insightId, UserPrincipal principal);
}
