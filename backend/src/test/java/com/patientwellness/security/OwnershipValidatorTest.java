package com.patientwellness.security;

import com.patientwellness.entity.Patient;
import com.patientwellness.entity.User;
import com.patientwellness.enums.Role;
import com.patientwellness.exception.ForbiddenException;
import com.patientwellness.exception.ResourceNotFoundException;
import com.patientwellness.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnershipValidatorTest {

    @Mock
    private PatientRepository patientRepository;

    private OwnershipValidator ownershipValidator;

    @BeforeEach
    void setUp() {
        ownershipValidator = new OwnershipValidator(patientRepository);
    }

    @Test
    @DisplayName("Should permit access when user owns the patient profile")
    void shouldPermitWhenUserOwnsPatient() {
        User owner = User.builder().fullName("Owner User").email("owner@test.com").role(Role.USER).build();
        owner.setId(1L);

        Patient patient = Patient.builder().user(owner).fullName("Child Patient").build();
        patient.setId(10L);

        UserPrincipal principal = new UserPrincipal(1L, "Owner User", "owner@test.com", "pass", Role.USER, true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));

        Patient validated = ownershipValidator.getAndValidatePatient(10L, principal);
        assertThat(validated).isNotNull();
        assertThat(validated.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user does NOT own patient")
    void shouldThrowForbiddenWhenUserDoesNotOwnPatient() {
        User owner = User.builder().fullName("Owner User").email("owner@test.com").role(Role.USER).build();
        owner.setId(1L);

        Patient patient = Patient.builder().user(owner).fullName("Child Patient").build();
        patient.setId(10L);

        UserPrincipal stranger = new UserPrincipal(2L, "Stranger User", "stranger@test.com", "pass", Role.USER, true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));

        assertThatThrownBy(() -> ownershipValidator.getAndValidatePatient(10L, stranger))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Access denied: You do not own this patient profile");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when patient does not exist")
    void shouldThrowNotFoundWhenPatientMissing() {
        UserPrincipal principal = new UserPrincipal(1L, "Owner User", "owner@test.com", "pass", Role.USER, true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownershipValidator.getAndValidatePatient(999L, principal))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found with id: 999");
    }

    @Test
    @DisplayName("Should allow ADMIN role to access patient")
    void shouldAllowAdminRole() {
        User owner = User.builder().fullName("Owner User").email("owner@test.com").role(Role.USER).build();
        owner.setId(1L);

        Patient patient = Patient.builder().user(owner).fullName("Child Patient").build();
        patient.setId(10L);

        UserPrincipal admin = new UserPrincipal(99L, "System Admin", "admin@test.com", "pass", Role.ADMIN, true,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));

        Patient validated = ownershipValidator.getAndValidatePatient(10L, admin);
        assertThat(validated).isNotNull();
    }
}
