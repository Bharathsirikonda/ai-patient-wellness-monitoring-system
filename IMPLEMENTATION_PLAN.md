# Implementation Plan: AI-Enabled Patient Wellness Monitoring System

## Phase 1: Architecture, Project Structure & Foundation
- [x] Analyze environment, tools, and prerequisites (Java 21, Maven 3.9.6, Git, gh CLI).
- [x] Author `ARCHITECTURE.md` with system diagrams, module boundaries, security model, and AI safety rules.
- [x] Author `IMPLEMENTATION_PLAN.md` detailing step-by-step development trajectory.
- [x] Author `PROGRESS.md` to track live progress and execution milestones.
- [ ] Establish standard `.gitignore` and Maven `pom.xml` configured for Java 21 and Spring Boot 3.3.x.
- [ ] Verify Maven compilation and dependency resolution.

## Phase 2: Core Domain Model, Persistence & Migrations
- [ ] Define Flyway database migration `V1__init_schema.sql` (Users, Patients, Wellness Records, Wellness Plans, Wellness Insights).
- [ ] Create JPA entity hierarchy:
  - `BaseEntity` (id, timestamps)
  - `User` (email, password, fullName, role, active)
  - `Patient` (user relationship, fullName, dateOfBirth, gender, height, weight, active)
  - `WellnessRecord` (patient relationship, recordDate, weight, sleepHours, waterIntake, exerciseMinutes, mood, energyLevel, stressLevel, notes)
  - `WellnessPlan` (patient relationship, title, targets, date range, status)
  - `WellnessInsight` (patient relationship, summary, positiveObservations, attentionAreas, suggestions, trends, provider, disclaimer, recordCountAnalyzed)
- [ ] Implement Spring Data JPA repositories with custom query methods and index optimization.

## Phase 3: Security & Stateless JWT Authentication
- [ ] Configure `SecurityConfig` with `SecurityFilterChain` permitting `/api/v1/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`, and `/actuator/health`.
- [ ] Implement `JwtTokenProvider` (HMAC-SHA, configurable secret, expiration, token claims).
- [ ] Implement `JwtAuthenticationFilter` and `JwtAuthenticationEntryPoint`.
- [ ] Implement `CustomUserDetailsService` and `UserPrincipal`.
- [ ] Implement `OwnershipValidator` to systematically enforce data tenancy across all endpoints.
- [ ] Create `AuthService` and `AuthController` (`/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/me`).

## Phase 4: Patient Management Subsystem
- [ ] Create Request/Response DTOs with Jakarta Bean Validation.
- [ ] Implement `PatientService` & `PatientServiceImpl` (CRUD, tenancy checks, soft deactivation).
- [ ] Implement `PatientController` with clean REST conventions.
- [ ] Enforce cross-user data isolation.

## Phase 5: Wellness Monitoring & Statistical Summaries
- [ ] Create Request/Response DTOs for daily wellness records.
- [ ] Implement `WellnessRecordService` (CRUD, date range queries, pagination, validation of physiological limits).
- [ ] Implement `StatisticsCalculator` & `WellnessSummaryService` (weekly averages, monthly summaries, weight delta, mood distributions, record completion rates).
- [ ] Implement `WellnessRecordController` and `WellnessSummaryController`.

## Phase 6: Wellness Plan & Target Tracking
- [ ] Create Request/Response DTOs for wellness plans.
- [ ] Implement `WellnessPlanService` (create, update, status transitions: ACTIVE, COMPLETED, CANCELLED, target adherence calculation).
- [ ] Implement `WellnessPlanController` (`/api/v1/patients/{patientId}/wellness-plans`, `/api/v1/wellness-plans/{planId}`).

## Phase 7: AI Wellness Insights & Privacy Guardrails
- [ ] Define `LLMProvider` interface and data contracts.
- [ ] Implement `PromptBuilder` with strict de-identification (no names, emails, or personal identifiers).
- [ ] Implement `OpenAiCompatibleLLMProvider` using Spring `RestClient` (non-blocking, timeout-guarded, configurable API URL and model).
- [ ] Implement `DeterministicFallbackLLMProvider` generating structured wellness insights based on heuristics (sleep deficits, hydration levels, activity trends) when no API key is supplied.
- [ ] Implement `WellnessInsightService` and `WellnessInsightController` (`/api/v1/patients/{patientId}/insights/generate`, retrieval endpoints).

## Phase 8: Centralized Error Handling & Validation
- [ ] Implement `GlobalExceptionHandler` with `@RestControllerAdvice`.
- [ ] Format structured, RFC-7807 problem details in `ApiResponse` / `ErrorResponse`.
- [ ] Intercept validation errors, authentication exceptions, resource not found, cross-tenant access denial, duplicate email, and unexpected runtime errors.

## Phase 9: Automated Testing & Verification
- [ ] Write Unit Tests for:
  - `AuthService` & `JwtTokenProvider`
  - `PatientService` & `OwnershipValidator`
  - `WellnessRecordService` & `StatisticsCalculator`
  - `WellnessPlanService`
  - `WellnessInsightService` & Fallback Provider
- [ ] Write Integration Tests (`@SpringBootTest`, `@AutoConfigureMockMvc`):
  - End-to-end Auth flow (Register, Login, Me)
  - Patient CRUD and Cross-User access denial (User A cannot access User B)
  - Wellness Record operations & physiological boundary validations
  - Wellness Summary calculations
  - Wellness Plan creation, status patch, and target progress
  - AI Insight generation and retrieval
- [ ] Execute `mvn clean test` and `mvn clean verify` to achieve 100% test pass rate.

## Phase 10: Docker, Configuration & Containerization
- [ ] Create `application.yml`, `application-test.yml`.
- [ ] Create `.env.example` with documented environment variables.
- [ ] Create production-ready multi-stage `Dockerfile`.
- [ ] Create `docker-compose.yml` with MySQL 8 service and Spring Boot container.

## Phase 11: API Documentation & Postman Collection
- [ ] Configure Springdoc OpenAPI / Swagger UI (`OpenApiConfig.java`).
- [ ] Provide comprehensive Postman collection `postman_collection.json` with ready-to-run demo sequence.

## Phase 12: GitHub Integration & Publication
- [ ] Initialize Git repository.
- [ ] Commit source code with clean atomic commits.
- [ ] Check / create public repository `Bharathsirikonda/ai-patient-wellness-monitoring-system`.
- [ ] Push to `main` branch on GitHub.
- [ ] Verify remote repository contents and commits.

## Phase 13: Final Verification & Portfolio Report
- [ ] Execute end-to-end verification.
- [ ] Author comprehensive `README.md` with architectural diagrams, setup instructions, curl examples, and portfolio presentation.
- [ ] Present comprehensive final delivery report.
