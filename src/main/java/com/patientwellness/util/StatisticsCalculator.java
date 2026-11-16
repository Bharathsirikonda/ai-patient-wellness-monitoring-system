package com.patientwellness.util;

import com.patientwellness.dto.response.WellnessSummaryResponse;
import com.patientwellness.entity.WellnessRecord;
import com.patientwellness.enums.Mood;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class StatisticsCalculator {

    private StatisticsCalculator() {}

    public static WellnessSummaryResponse calculateSummary(
            Long patientId,
            String period,
            LocalDate startDate,
            LocalDate endDate,
            List<WellnessRecord> records
    ) {
        long totalDaysInPeriod = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalRecordsLogged = records.size();

        BigDecimal completionRate = totalDaysInPeriod > 0
                ? BigDecimal.valueOf(totalRecordsLogged * 100.0 / totalDaysInPeriod).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        if (records.isEmpty()) {
            return WellnessSummaryResponse.builder()
                    .patientId(patientId)
                    .period(period)
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalDaysInPeriod(totalDaysInPeriod)
                    .totalRecordsLogged(0)
                    .completionRatePercentage(BigDecimal.ZERO)
                    .averageSleepHours(BigDecimal.ZERO)
                    .averageWaterIntakeMl(BigDecimal.ZERO)
                    .averageExerciseMinutesPerDay(BigDecimal.ZERO)
                    .totalExerciseMinutes(0)
                    .startWeight(null)
                    .latestWeight(null)
                    .weightChangeKg(BigDecimal.ZERO)
                    .averageEnergyLevel(BigDecimal.ZERO)
                    .averageStressLevel(BigDecimal.ZERO)
                    .moodDistribution(Map.of())
                    .summaryObservation("No wellness records found in the specified timeframe.")
                    .build();
        }

        // Sleep
        BigDecimal totalSleep = records.stream()
                .map(WellnessRecord::getSleepHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgSleep = totalSleep.divide(BigDecimal.valueOf(totalRecordsLogged), 2, RoundingMode.HALF_UP);

        // Water
        double totalWater = records.stream()
                .mapToInt(WellnessRecord::getWaterIntake)
                .sum();
        BigDecimal avgWater = BigDecimal.valueOf(totalWater / totalRecordsLogged).setScale(1, RoundingMode.HALF_UP);

        // Exercise
        int totalExercise = records.stream()
                .mapToInt(WellnessRecord::getExerciseMinutes)
                .sum();
        BigDecimal avgExercise = BigDecimal.valueOf((double) totalExercise / totalRecordsLogged).setScale(1, RoundingMode.HALF_UP);

        // Weight
        List<WellnessRecord> sortedByDate = records.stream()
                .sorted(Comparator.comparing(WellnessRecord::getRecordDate))
                .toList();

        List<BigDecimal> weights = sortedByDate.stream()
                .map(WellnessRecord::getWeight)
                .filter(w -> w != null && w.compareTo(BigDecimal.ZERO) > 0)
                .toList();

        BigDecimal startWeight = weights.isEmpty() ? null : weights.get(0);
        BigDecimal latestWeight = weights.isEmpty() ? null : weights.get(weights.size() - 1);
        BigDecimal weightChange = (startWeight != null && latestWeight != null)
                ? latestWeight.subtract(startWeight).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Energy & Stress
        double totalEnergy = records.stream().mapToInt(WellnessRecord::getEnergyLevel).sum();
        BigDecimal avgEnergy = BigDecimal.valueOf(totalEnergy / totalRecordsLogged).setScale(1, RoundingMode.HALF_UP);

        double totalStress = records.stream().mapToInt(WellnessRecord::getStressLevel).sum();
        BigDecimal avgStress = BigDecimal.valueOf(totalStress / totalRecordsLogged).setScale(1, RoundingMode.HALF_UP);

        // Mood distribution
        Map<Mood, Long> moodDist = records.stream()
                .collect(Collectors.groupingBy(WellnessRecord::getMood, Collectors.counting()));

        // Qualitative observation
        String observation = buildObservation(avgSleep, avgWater, avgExercise, avgEnergy, avgStress, completionRate);

        return WellnessSummaryResponse.builder()
                .patientId(patientId)
                .period(period)
                .startDate(startDate)
                .endDate(endDate)
                .totalDaysInPeriod(totalDaysInPeriod)
                .totalRecordsLogged(totalRecordsLogged)
                .completionRatePercentage(completionRate)
                .averageSleepHours(avgSleep)
                .averageWaterIntakeMl(avgWater)
                .averageExerciseMinutesPerDay(avgExercise)
                .totalExerciseMinutes(totalExercise)
                .startWeight(startWeight)
                .latestWeight(latestWeight)
                .weightChangeKg(weightChange)
                .averageEnergyLevel(avgEnergy)
                .averageStressLevel(avgStress)
                .moodDistribution(moodDist)
                .summaryObservation(observation)
                .build();
    }

    private static String buildObservation(
            BigDecimal avgSleep,
            BigDecimal avgWater,
            BigDecimal avgExercise,
            BigDecimal avgEnergy,
            BigDecimal avgStress,
            BigDecimal completionRate
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Log completion rate is %s%%. ", completionRate));

        if (avgSleep.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
            sb.append(String.format("Sleep is healthy (avg %s hrs/night). ", avgSleep));
        } else {
            sb.append(String.format("Sleep is below optimal target (avg %s hrs/night). ", avgSleep));
        }

        if (avgWater.compareTo(BigDecimal.valueOf(2000)) >= 0) {
            sb.append("Hydration target adequately maintained. ");
        } else {
            sb.append("Hydration could benefit from an increase towards 2000+ mL. ");
        }

        if (avgExercise.compareTo(BigDecimal.valueOf(30)) >= 0) {
            sb.append(String.format("Active routine maintained with %s mins/day of activity. ", avgExercise));
        } else {
            sb.append("Physical activity is moderate. ");
        }

        sb.append(String.format("Average subjective energy was %s/10 and stress level was %s/10.", avgEnergy, avgStress));
        return sb.toString();
    }
}
