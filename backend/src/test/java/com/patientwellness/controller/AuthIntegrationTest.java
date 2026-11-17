package com.patientwellness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientwellness.dto.request.AuthLoginRequest;
import com.patientwellness.dto.request.AuthRegisterRequest;
import com.patientwellness.enums.Role;
import com.patientwellness.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully register a new user and return JWT token without exposing password")
    void testRegisterSuccess() throws Exception {
        AuthRegisterRequest request = AuthRegisterRequest.builder()
                .fullName("Jordan Smith")
                .email("jordan.smith@example.com")
                .password("SecurePass99#")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.email").value("jordan.smith@example.com"))
                .andExpect(jsonPath("$.data.user.password").doesNotExist());
    }

    @Test
    @DisplayName("Should reject registration with invalid email or weak password")
    void testRegisterValidationFailure() throws Exception {
        AuthRegisterRequest request = AuthRegisterRequest.builder()
                .fullName("J")
                .email("invalid-email")
                .password("weak")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.validationErrors").isArray());
    }

    @Test
    @DisplayName("Should reject duplicate registration with 409 Conflict")
    void testRegisterDuplicateEmail() throws Exception {
        AuthRegisterRequest request = AuthRegisterRequest.builder()
                .fullName("Jordan Smith")
                .email("jordan.smith@example.com")
                .password("SecurePass99#")
                .role(Role.USER)
                .build();

        // First registration
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate registration attempt
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("Should authenticate registered user and return JWT")
    void testLoginSuccess() throws Exception {
        AuthRegisterRequest registerRequest = AuthRegisterRequest.builder()
                .fullName("Jordan Smith")
                .email("jordan.login@example.com")
                .password("SecurePass99#")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        AuthLoginRequest loginRequest = AuthLoginRequest.builder()
                .email("jordan.login@example.com")
                .password("SecurePass99#")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isString());
    }

    @Test
    @DisplayName("Should reject invalid login credentials with 401 Unauthorized")
    void testLoginInvalidCredentials() throws Exception {
        AuthLoginRequest loginRequest = AuthLoginRequest.builder()
                .email("nonexistent@example.com")
                .password("WrongPassword123!")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
