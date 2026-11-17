package com.patientwellness.repository;

import com.patientwellness.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByUserIdAndActiveTrue(Long userId);

    Page<Patient> findByUserIdAndActiveTrue(Long userId, Pageable pageable);

    Optional<Patient> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
