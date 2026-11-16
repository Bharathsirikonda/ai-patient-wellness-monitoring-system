package com.patientwellness.service;

import com.patientwellness.dto.request.AuthLoginRequest;
import com.patientwellness.dto.request.AuthRegisterRequest;
import com.patientwellness.dto.response.AuthResponse;
import com.patientwellness.entity.User;
import com.patientwellness.enums.Role;
import com.patientwellness.exception.DuplicateResourceException;
import com.patientwellness.repository.UserRepository;
import com.patientwellness.security.JwtTokenProvider;
import com.patientwellness.security.UserPrincipal;
import com.patientwellness.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder, authenticationManager, tokenProvider);
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUser() {
        AuthRegisterRequest request = AuthRegisterRequest.builder()
                .fullName("Alex Morgan")
                .email("alex@example.com")
                .password("StrongPass123!")
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail("alex@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPass123!")).thenReturn("encoded_pass");

        User savedUser = User.builder()
                .fullName("Alex Morgan")
                .email("alex@example.com")
                .password("encoded_pass")
                .role(Role.USER)
                .active(true)
                .build();
        savedUser.setId(10L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenProvider.generateTokenForUser(10L, "alex@example.com", "USER", "Alex Morgan")).thenReturn("jwt.token.here");
        when(tokenProvider.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt.token.here");
        assertThat(response.getUser().getEmail()).isEqualTo("alex@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void shouldThrowWhenEmailDuplicate() {
        AuthRegisterRequest request = AuthRegisterRequest.builder()
                .fullName("Alex Morgan")
                .email("alex@example.com")
                .password("StrongPass123!")
                .build();

        when(userRepository.existsByEmail("alex@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should login and issue token on valid credentials")
    void shouldLoginSuccessfully() {
        AuthLoginRequest request = AuthLoginRequest.builder()
                .email("alex@example.com")
                .password("StrongPass123!")
                .build();

        User user = User.builder()
                .fullName("Alex Morgan")
                .email("alex@example.com")
                .password("encoded_pass")
                .role(Role.USER)
                .active(true)
                .build();
        user.setId(10L);

        UserPrincipal principal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt.login.token");
        when(tokenProvider.getExpirationMs()).thenReturn(86400000L);
        when(userRepository.findByEmail("alex@example.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt.login.token");
        assertThat(response.getUser().getId()).isEqualTo(10L);
    }
}
