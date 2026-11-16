package com.patientwellness.repository;

import com.patientwellness.entity.WellnessPlan;
import com.patientwellness.enums.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WellnessPlanRepository extends JpaRepository<WellnessPlan, Long> {

    List<WellnessPlan> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<WellnessPlan> findByPatientIdAndStatus(Long patientId, PlanStatus status);

    Optional<WellnessPlan> findByIdAndPatientId(Long id, Long patientId);
}
