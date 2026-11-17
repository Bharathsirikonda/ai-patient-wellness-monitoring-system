package com.patientwellness.entity;

import com.patientwellness.enums.Mood;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "wellness_records",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_patient_record_date", columnNames = {"patient_id", "record_date"})
        }
)
public class WellnessRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "weight", precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "sleep_hours", nullable = false, precision = 4, scale = 2)
    private BigDecimal sleepHours;

    @Column(name = "water_intake", nullable = false)
    private Integer waterIntake; // in milliliters (mL)

    @Column(name = "exercise_minutes", nullable = false)
    private Integer exerciseMinutes; // in minutes

    @Enumerated(EnumType.STRING)
    @Column(name = "mood", nullable = false, length = 30)
    private Mood mood;

    @Column(name = "energy_level", nullable = false)
    private Integer energyLevel; // 1 to 10

    @Column(name = "stress_level", nullable = false)
    private Integer stressLevel; // 1 to 10

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
