package com.patientwellness.integration.llm;

import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.Gender;
import com.patientwellness.enums.Mood;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DeterministicFallbackLLMProvider implements LLMProvider {

    public static final String PROVIDER_NAME = "DETERMINISTIC_FALLBACK (Demo/Offline Mode)";
    public static final String MANDATORY_DISCLAIMER =
            "AI Wellness Insights are generated for motivational and lifestyle educational purposes only. "
            + "They do not constitute medical diagnosis, clinical treatment, prescription, or healthcare professional advice.";

    @Override
    public InsightGenerationResult generateInsights(
            List<WellnessRecord> records,
            LocalDate dateOfBirth,
            Gender gender
    ) {
        if (records == null || records.isEmpty()) {
            return InsightGenerationResult.builder()
                    .summary("Insufficient historical wellness records available. Please log at least 2 daily entries to generate meaningful lifestyle insights.")
                    .positiveObservations(List.of("Profile created successfully and ready for daily wellness tracking."))
                    .attentionAreas(List.of("No daily wellness logs recorded in the selected observation window."))
                    .suggestions(List.of(
                            "Record your daily sleep duration, hydration, and activity each evening.",
                            "Aim for at least 7-8 hours of sleep and 2000 mL of water daily."
                    ))
                    .trends(List.of("Historical trend analysis requires additional daily logs."))
                    .providerName(PROVIDER_NAME)
                    .disclaimer(MANDATORY_DISCLAIMER)
                    .build();
        }

        int count = records.size();

        // Statistical heuristics
        BigDecimal totalSleep = records.stream().map(WellnessRecord::getSleepHours).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgSleep = totalSleep.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        double avgWater = records.stream().mapToInt(WellnessRecord::getWaterIntake).average().orElse(0.0);
        double avgExercise = records.stream().mapToInt(WellnessRecord::getExerciseMinutes).average().orElse(0.0);
        double avgEnergy = records.stream().mapToInt(WellnessRecord::getEnergyLevel).average().orElse(0.0);
        double avgStress = records.stream().mapToInt(WellnessRecord::getStressLevel).average().orElse(0.0);

        Map<Mood, Long> moodCounts = records.stream()
                .collect(Collectors.groupingBy(WellnessRecord::getMood, Collectors.counting()));

        List<String> positives = new ArrayList<>();
        List<String> attention = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        List<String> trends = new ArrayList<>();

        // Sleep Analysis
        if (avgSleep.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
            positives.add(String.format("Healthy rest pattern maintained with an average of %s hours of sleep per night.", avgSleep));
        } else {
            attention.add(String.format("Average sleep duration (%s hrs) is lower than the recommended 7–9 hour restorative range.", avgSleep));
            suggestions.add("Establish a screen-free wind-down routine 45 minutes prior to sleep to improve sleep quality.");
        }

        // Hydration Analysis
        if (avgWater >= 2000) {
            positives.add(String.format("Optimal hydration achieved with an average of %.0f mL of water consumed daily.", avgWater));
        } else {
            attention.add(String.format("Daily water intake averaged %.0f mL, which falls short of the recommended 2000–2500 mL baseline.", avgWater));
            suggestions.add("Keep a reusable water bottle nearby and target consuming a full glass upon waking and before each meal.");
        }

        // Physical Activity Analysis
        if (avgExercise >= 30) {
            positives.add(String.format("Active lifestyle: logged an average of %.0f minutes of physical activity daily.", avgExercise));
        } else {
            attention.add(String.format("Physical activity averaged %.0f minutes per day. Moderate aerobic activity promotes cardiovascular resilience.", avgExercise));
            suggestions.add("Introduce a brisk 20-minute walk during lunch breaks or evenings to steadily build endurance.");
        }

        // Stress & Energy Trends
        if (avgEnergy >= 7.0) {
            positives.add(String.format("Robust self-reported energy levels averaging %.1f/10 across logged days.", avgEnergy));
        }
        if (avgStress >= 6.0) {
            attention.add(String.format("Elevated average stress levels (%.1f/10) reported across the monitoring interval.", avgStress));
            suggestions.add("Practice short diaphragmatic breathing exercises or guided mindfulness for 5–10 minutes during peak stress hours.");
        }

        // Mood synthesis
        long positiveMoodCount = moodCounts.getOrDefault(Mood.EXCELLENT, 0L) + moodCounts.getOrDefault(Mood.GOOD, 0L);
        if (positiveMoodCount > count / 2) {
            trends.add("Mood trends are predominantly positive, reflecting emotional equilibrium across the logging period.");
        } else {
            trends.add("Emotional wellbeing fluctuates; fluctuations often correspond to variations in sleep and daily activity levels.");
        }

        trends.add(String.format("Evaluated %d daily wellness entries; data indicates lifestyle regularity in logging habits.", count));

        String summary = String.format(
                "Analysis of %d wellness record(s) indicates an average of %s hours of nightly sleep, %.0f mL daily hydration, and %.0f minutes of physical activity. Subjective vitality averaged %.1f/10 with an average stress score of %.1f/10.",
                count, avgSleep, avgWater, avgExercise, avgEnergy, avgStress
        );

        if (positives.isEmpty()) {
            positives.add("Active commitment to tracking daily health markers.");
        }
        if (attention.isEmpty()) {
            attention.add("All monitored wellness parameters remain balanced within healthy guidelines.");
        }
        if (suggestions.isEmpty()) {
            suggestions.add("Continue your balanced daily routine of consistent sleep, adequate hydration, and moderate movement.");
        }

        return InsightGenerationResult.builder()
                .summary(summary)
                .positiveObservations(positives)
                .attentionAreas(attention)
                .suggestions(suggestions)
                .trends(trends)
                .providerName(PROVIDER_NAME)
                .disclaimer(MANDATORY_DISCLAIMER)
                .build();
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
