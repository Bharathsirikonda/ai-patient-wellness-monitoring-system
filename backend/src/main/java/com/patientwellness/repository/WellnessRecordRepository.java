package com.patientwellness.repository;

import com.patientwellness.entity.WellnessRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WellnessRecordRepository extends JpaRepository<WellnessRecord, Long> {

    Page<WellnessRecord> findByPatientIdOrderByRecordDateDesc(Long patientId, Pageable pageable);

    List<WellnessRecord> findByPatientIdOrderByRecordDateDesc(Long patientId);

    List<WellnessRecord> findByPatientIdOrderByRecordDateAsc(Long patientId);

    List<WellnessRecord> findByPatientIdAndRecordDateBetweenOrderByRecordDateAsc(
            Long patientId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<WellnessRecord> findByIdAndPatientId(Long id, Long patientId);

    boolean existsByPatientIdAndRecordDate(Long patientId, LocalDate recordDate);

    boolean existsByPatientIdAndRecordDateAndIdNot(Long patientId, LocalDate recordDate, Long id);
}
