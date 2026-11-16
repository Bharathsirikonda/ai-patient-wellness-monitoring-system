package com.patientwellness.integration.llm;

import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public interface LLMProvider {
    InsightGenerationResult generateInsights(List<WellnessRecord> records, LocalDate dateOfBirth, Gender gender);
    String getProviderName();
    boolean isAvailable();
}
