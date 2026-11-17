package com.patientwellness.repository;

import com.patientwellness.entity.WellnessInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WellnessInsightRepository extends JpaRepository<WellnessInsight, Long> {

    List<WellnessInsight> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    Optional<WellnessInsight> findByIdAndPatientId(Long id, Long patientId);
}
