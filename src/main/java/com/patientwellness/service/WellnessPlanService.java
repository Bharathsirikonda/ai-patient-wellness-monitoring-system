package com.patientwellness.service;

import com.patientwellness.dto.request.WellnessPlanRequest;
import com.patientwellness.dto.response.WellnessPlanProgressResponse;
import com.patientwellness.dto.response.WellnessPlanResponse;
import com.patientwellness.enums.PlanStatus;
import com.patientwellness.security.UserPrincipal;

import java.util.List;

public interface WellnessPlanService {
    WellnessPlanResponse createPlan(Long patientId, WellnessPlanRequest request, UserPrincipal principal);
    WellnessPlanResponse getPlanById(Long planId, UserPrincipal principal);
    List<WellnessPlanResponse> getPlansForPatient(Long patientId, UserPrincipal principal);
    WellnessPlanResponse updatePlan(Long planId, WellnessPlanRequest request, UserPrincipal principal);
    WellnessPlanResponse updatePlanStatus(Long planId, PlanStatus status, UserPrincipal principal);
    WellnessPlanProgressResponse getPlanProgress(Long planId, UserPrincipal principal);
}
