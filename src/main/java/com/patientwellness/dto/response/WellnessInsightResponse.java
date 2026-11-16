package com.patientwellness.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WellnessInsightResponse {
    private Long id;
    private Long patientId;
    private String summary;
    private List<String> positiveObservations;
    private List<String> attentionAreas;
    private List<String> suggestions;
    private List<String> trends;
    private String provider;
    private String disclaimer;
    private Integer recordsAnalyzedCount;
    private LocalDateTime createdAt;
}
