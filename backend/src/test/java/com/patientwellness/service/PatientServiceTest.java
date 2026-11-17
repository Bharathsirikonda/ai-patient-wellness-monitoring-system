package com.patientwellness.service;

import com.patientwellness.dto.request.PatientCreateRequest;
import com.patientwellness.dto.request.PatientUpdateRequest;
import com.patientwellness.dto.response.PatientResponse;
import com.patientwellness.entity.Patient;
import com.patientwellness.entity.User;
import com.patientwellness.enums.Gender;
import com.patientwellness.enums.Role;
import com.patientwellness.repository.PatientRepository;
import com.patientwellness.repository.UserRepository;
import com.patientwellness.security.OwnershipValidator;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OwnershipValidator ownershipValidator;

    private PatientService patientService;
    private UserPrincipal principal;
    private User user;

    @BeforeEach
    void setUp() {
        patientService = new PatientServiceImpl(patientRepository, userRepository, ownershipValidator);

        user = User.builder()
                .fullName("Alex Morgan")
                .email("alex@example.com")
                .password("encoded")
                .role(Role.USER)
                .active(true)
                .build();
        user.setId(1L);

        principal = new UserPrincipal(1L, "Alex Morgan", "alex@example.com", "encoded", Role.USER, true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("Should create patient linked to authenticated user")
    void shouldCreatePatient() {
        PatientCreateRequest request = PatientCreateRequest.builder()
                .fullName("Taylor Morgan")
                .dateOfBirth(LocalDate.of(2010, 5, 20))
                .gender(Gender.FEMALE)
                .height(new BigDecimal("150.00"))
                .weight(new BigDecimal("45.00"))
                .build();

        Patient saved = Patient.builder()
                .user(user)
                .fullName("Taylor Morgan")
                .dateOfBirth(LocalDate.of(2010, 5, 20))
                .gender(Gender.FEMALE)
                .height(new BigDecimal("150.00"))
                .weight(new BigDecimal("45.00"))
                .active(true)
                .build();
        saved.setId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        PatientResponse response = patientService.createPatient(request, principal);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getFullName()).isEqualTo("Taylor Morgan");
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should update patient profile")
    void shouldUpdatePatient() {
        Patient existing = Patient.builder()
                .user(user)
                .fullName("Old Name")
                .dateOfBirth(LocalDate.of(2010, 5, 20))
                .active(true)
                .build();
        existing.setId(10L);

        when(ownershipValidator.getAndValidatePatient(10L, principal)).thenReturn(existing);
        when(patientRepository.save(existing)).thenReturn(existing);

        PatientUpdateRequest updateRequest = PatientUpdateRequest.builder()
                .fullName("New Name")
                .dateOfBirth(LocalDate.of(2010, 5, 20))
                .gender(Gender.MALE)
                .height(new BigDecimal("155.00"))
                .weight(new BigDecimal("48.00"))
                .active(true)
                .build();

        PatientResponse updated = patientService.updatePatient(10L, updateRequest, principal);

        assertThat(updated.getFullName()).isEqualTo("New Name");
        assertThat(updated.getGender()).isEqualTo(Gender.MALE);
    }
}
