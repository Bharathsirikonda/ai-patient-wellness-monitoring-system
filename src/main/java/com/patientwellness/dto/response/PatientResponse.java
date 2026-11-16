package com.patientwellness.dto.response;

import com.patientwellness.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private BigDecimal height;
    private BigDecimal weight;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
