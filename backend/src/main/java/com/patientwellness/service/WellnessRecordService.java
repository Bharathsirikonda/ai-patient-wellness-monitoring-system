package com.patientwellness.service;

import com.patientwellness.dto.request.WellnessRecordRequest;
import com.patientwellness.dto.response.WellnessRecordResponse;
import com.patientwellness.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface WellnessRecordService {
    WellnessRecordResponse createRecord(Long patientId, WellnessRecordRequest request, UserPrincipal principal);
    WellnessRecordResponse getRecordById(Long patientId, Long recordId, UserPrincipal principal);
    Page<WellnessRecordResponse> getRecordsForPatient(Long patientId, Pageable pageable, UserPrincipal principal);
    List<WellnessRecordResponse> getRecordsForDateRange(Long patientId, LocalDate startDate, LocalDate endDate, UserPrincipal principal);
    WellnessRecordResponse updateRecord(Long patientId, Long recordId, WellnessRecordRequest request, UserPrincipal principal);
    void deleteRecord(Long patientId, Long recordId, UserPrincipal principal);
    List<WellnessRecordResponse> getRecentRecords(Long patientId, int limit, UserPrincipal principal);
}
