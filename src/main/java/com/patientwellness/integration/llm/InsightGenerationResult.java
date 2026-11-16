package com.patientwellness.integration.llm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsightGenerationResult {
    private String summary;
    private List<String> positiveObservations;
    private List<String> attentionAreas;
    private List<String> suggestions;
    private List<String> trends;
    private String providerName;
    private String disclaimer;
}
