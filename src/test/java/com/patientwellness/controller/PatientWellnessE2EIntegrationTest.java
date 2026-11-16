package com.patientwellness.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientwellness.dto.request.AuthLoginRequest;
import com.patientwellness.dto.request.AuthRegisterRequest;
import com.patientwellness.dto.request.PatientCreateRequest;
import com.patientwellness.dto.request.WellnessPlanRequest;
import com.patientwellness.dto.request.WellnessRecordRequest;
import com.patientwellness.enums.Gender;
import com.patientwellness.enums.Mood;
import com.patientwellness.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientWellnessE2EIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Complete E2E workflow: Authentication, Patient CRUD, Records, Analytics, Plans, AI Insights, and Cross-Tenant Security Isolation")
    void testCompleteSystemFlow() throws Exception {
        // 1. Register User A
        AuthRegisterRequest regA = AuthRegisterRequest.builder()
                .fullName("User Primary")
                .email("user.primary@example.com")
                .password("Password123#")
                .role(Role.USER)
                .build();

        MvcResult regResultA = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regA)))
                .andExpect(status().isCreated())
                .andReturn();

        String tokenA = extractToken(regResultA);
        assertThat(tokenA).isNotBlank();

        // 2. Register User B (Adversary / Different Tenant)
        AuthRegisterRequest regB = AuthRegisterRequest.builder()
                .fullName("User Secondary")
                .email("user.secondary@example.com")
                .password("Password123#")
                .role(Role.USER)
                .build();

        MvcResult regResultB = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regB)))
                .andExpect(status().isCreated())
                .andReturn();

        String tokenB = extractToken(regResultB);
        assertThat(tokenB).isNotBlank();

        // 3. User A creates a Patient profile
        PatientCreateRequest createPat = PatientCreateRequest.builder()
                .fullName("Alice Primary Patient")
                .dateOfBirth(LocalDate.of(1992, 6, 12))
                .gender(Gender.FEMALE)
                .height(new BigDecimal("165.00"))
                .weight(new BigDecimal("60.00"))
                .build();

        MvcResult patResult = mockMvc.perform(post("/api/v1/patients")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPat)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.fullName").value("Alice Primary Patient"))
                .andReturn();

        Long patientId = extractId(patResult);
        assertThat(patientId).isNotNull();

        // 4. User A logs a Wellness Record
        WellnessRecordRequest recordReq = WellnessRecordRequest.builder()
                .recordDate(LocalDate.now())
                .sleepHours(new BigDecimal("7.50"))
                .waterIntake(2200)
                .exerciseMinutes(40)
                .weight(new BigDecimal("59.80"))
                .mood(Mood.GOOD)
                .energyLevel(8)
                .stressLevel(3)
                .notes("Morning yoga session completed.")
                .build();

        MvcResult recordResult = mockMvc.perform(post("/api/v1/patients/" + patientId + "/wellness-records")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.waterIntake").value(2200))
                .andReturn();

        Long recordId = extractId(recordResult);
        assertThat(recordId).isNotNull();

        // 5. User A retrieves wellness summaries
        mockMvc.perform(get("/api/v1/patients/" + patientId + "/wellness-summary/weekly")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalRecordsLogged").value(1))
                .andExpect(jsonPath("$.data.averageWaterIntakeMl").value(2200.0));

        // 6. User A creates a Wellness Plan
        WellnessPlanRequest planReq = WellnessPlanRequest.builder()
                .title("Optimal Hydration and Activity Plan")
                .description("Aim for 2000+ mL hydration and 150 mins exercise weekly.")
                .dailyWaterTarget(2000)
                .dailySleepTarget(new BigDecimal("8.00"))
                .weeklyExerciseTarget(150)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(14))
                .build();

        MvcResult planResult = mockMvc.perform(post("/api/v1/patients/" + patientId + "/wellness-plans")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Optimal Hydration and Activity Plan"))
                .andReturn();

        Long planId = extractId(planResult);
        assertThat(planId).isNotNull();

        // 7. User A tracks plan progress
        mockMvc.perform(get("/api/v1/wellness-plans/" + planId + "/progress")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.adherenceScorePercentage").isNumber());

        // 8. User A generates AI Wellness Insight (using fallback provider)
        mockMvc.perform(post("/api/v1/patients/" + patientId + "/insights/generate")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.summary").isString())
                .andExpect(jsonPath("$.data.disclaimer").isString())
                .andExpect(jsonPath("$.data.provider").value("DETERMINISTIC_FALLBACK (Demo/Offline Mode)"));

        // 9. CROSS-TENANT SECURITY ISOLATION VERIFICATION
        // User B attempts to access User A's Patient -> must return 403 Forbidden
        mockMvc.perform(get("/api/v1/patients/" + patientId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));

        // User B attempts to access User A's Wellness Records -> must return 403 Forbidden
        mockMvc.perform(get("/api/v1/patients/" + patientId + "/wellness-records/" + recordId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());

        // User B attempts to access User A's Wellness Plan -> must return 403 Forbidden
        mockMvc.perform(get("/api/v1/wellness-plans/" + planId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());

        // User B attempts to generate insights for User A's patient -> must return 403 Forbidden
        mockMvc.perform(post("/api/v1/patients/" + patientId + "/insights/generate")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());

        // 10. Unauthenticated request to protected endpoints -> must return 401 Unauthorized
        mockMvc.perform(get("/api/v1/patients/" + patientId))
                .andExpect(status().isUnauthorized());

        // 11. User A deactivates patient profile
        mockMvc.perform(delete("/api/v1/patients/" + patientId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());
    }

    private String extractToken(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        return root.path("data").path("accessToken").asText();
    }

    private Long extractId(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        return root.path("data").path("id").asLong();
    }
}
