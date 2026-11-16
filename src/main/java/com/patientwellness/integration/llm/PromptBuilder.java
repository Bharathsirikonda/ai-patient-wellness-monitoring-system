package com.patientwellness.integration.llm;

import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.Gender;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
public class PromptBuilder {

    public String buildSystemPrompt() {
        return """
                You are an empathetic, evidence-based lifestyle wellness assistant.
                
                CRITICAL MEDICAL SAFETY AND ETHICAL BOUNDARIES:
                1. You are NOT a medical doctor. DO NOT provide medical diagnoses.
                2. DO NOT prescribe medications, medical treatments, or diagnostic tests.
                3. DO NOT claim to replace professional healthcare providers.
                4. Always formulate suggestions as general wellness, lifestyle, and habit optimizations.
                5. Output strictly formatted JSON matching the requested schema.
                """;
    }

    public String buildUserPrompt(List<WellnessRecord> records, LocalDate dateOfBirth, Gender gender) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze the following anonymized wellness log series and return structured wellness insights.\n\n");

        if (dateOfBirth != null) {
            int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
            sb.append("Anonymized Demographic: Age ").append(age);
            if (gender != null) {
                sb.append(", Gender ").append(gender.name());
            }
            sb.append("\n");
        }

        sb.append("Total Days of Records: ").append(records.size()).append("\n\n");
        sb.append("Daily Logs:\n");

        for (WellnessRecord r : records) {
            sb.append(String.format(
                    "- Date: %s | Sleep: %s hrs | Water: %d mL | Exercise: %d mins | Mood: %s | Energy: %d/10 | Stress: %d/10\n",
                    r.getRecordDate(),
                    r.getSleepHours(),
                    r.getWaterIntake(),
                    r.getExerciseMinutes(),
                    r.getMood().name(),
                    r.getEnergyLevel(),
                    r.getStressLevel()
            ));
        }

        sb.append("""
                
                Please return your analysis as a valid JSON object with the following exact keys:
                {
                  "summary": "Short 2-3 sentence overview of the individual's lifestyle pattern",
                  "positiveObservations": ["Observation 1", "Observation 2"],
                  "attentionAreas": ["Area needing care 1", "Area needing care 2"],
                  "suggestions": ["Actionable habit tip 1", "Actionable habit tip 2"],
                  "trends": ["Trend observed over time 1", "Trend observed over time 2"]
                }
                """);

        return sb.toString();
    }
}
