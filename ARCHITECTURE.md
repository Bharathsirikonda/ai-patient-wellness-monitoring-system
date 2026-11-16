# Architecture Documentation: AI-Enabled Patient Wellness Monitoring System

## 1. System Overview

The **AI-Enabled Patient Wellness Monitoring System** is an enterprise-grade, production-style Spring Boot 3 backend application engineered on Java 21. It provides secure patient wellness tracking, comprehensive daily logs (sleep, hydration, physical activity, emotional state, stress levels), wellness plan management with adherence targets, statistical aggregations (weekly, monthly, trends), and an AI-driven Wellness Insights subsystem built around strict safety boundaries, privacy safeguards (zero PII exposure), and deterministic fallback mechanisms.

---

## 2. High-Level Architecture Diagram

```mermaid
graph TD
    Client["Client Applications<br/>(Web / Mobile / Postman / Swagger UI)"]

    subgraph Security Boundary ["Security & Ingress Boundary"]
        CORS["CORS & Security Filter Chain"]
        JWTFilter["JwtAuthenticationFilter"]
        SecurityContext["Spring Security Context<br/>(Stateless UserPrincipal)"]
    end

    subgraph API Layer ["REST Controllers (/api/v1)"]
        AuthController["AuthController (/api/v1/auth)"]
        PatientController["PatientController (/api/v1/patients)"]
        RecordController["WellnessRecordController (/api/v1/patients/.../wellness-records)"]
        SummaryController["WellnessSummaryController (/api/v1/patients/.../wellness-summary)"]
        PlanController["WellnessPlanController (/api/v1/.../wellness-plans)"]
        InsightController["WellnessInsightController (/api/v1/.../insights)"]
        Actuator["Actuator (/actuator/health)"]
    end

    subgraph Service Layer ["Business & Domain Services"]
        AuthService["AuthService"]
        PatientService["PatientService & OwnershipValidator"]
        RecordService["WellnessRecordService"]
        SummaryService["WellnessSummaryService"]
        PlanService["WellnessPlanService"]
        InsightService["WellnessInsightService"]
        LLMProvider["LLMProvider (Interface)"]
        OpenAiLLM["OpenAiCompatibleLLMProvider"]
        FallbackLLM["DeterministicFallbackLLMProvider"]
    end

    subgraph Data Access Layer ["Persistence & Data Access"]
        UserRepo["UserRepository"]
        PatientRepo["PatientRepository"]
        RecordRepo["WellnessRecordRepository"]
        PlanRepo["WellnessPlanRepository"]
        InsightRepo["WellnessInsightRepository"]
        Flyway["Flyway Database Migrations"]
    end

    subgraph Database Layer ["Storage Layer"]
        MySQL[("MySQL 8.x Database<br/>InnoDB Engine, UTF8mb4")]
    end

    Client --> CORS --> JWTFilter --> SecurityContext --> API Layer
    AuthController --> AuthService
    PatientController --> PatientService
    RecordController --> RecordService
    SummaryController --> SummaryService
    PlanController --> PlanService
    InsightController --> InsightService

    AuthService --> UserRepo
    PatientService --> PatientRepo
    RecordService --> RecordRepo
    SummaryService --> RecordRepo
    PlanService --> PlanRepo
    InsightService --> InsightRepo
    InsightService --> LLMProvider
    LLMProvider --> OpenAiLLM
    LLMProvider --> FallbackLLM

    UserRepo --> MySQL
    PatientRepo --> MySQL
    RecordRepo --> MySQL
    PlanRepo --> MySQL
    InsightRepo --> MySQL
    Flyway --> MySQL
```

---

## 3. Layered Design and Responsibilities

### 3.1 Ingress & Security
- **Stateless JWT Authentication**: Validates HMAC-SHA signed tokens via `JwtAuthenticationFilter`. Extracted credentials populate Spring's `SecurityContext`.
- **Role-Based Authorization**: Delineates `ROLE_USER` and `ROLE_ADMIN`.
- **Resource Ownership Validation**: In addition to standard endpoint-level `@PreAuthorize`, domain services and the dedicated `OwnershipValidator` enforce strict tenancy: normal users may only access, modify, or query patients, records, plans, and insights owned directly by their account. Cross-tenant leakage is systematically blocked with `403 Forbidden` or `404 Not Found`.

### 3.2 Presentation Layer (Controllers & DTOs)
- **Base URI Path**: Uniform `/api/v1` namespace.
- **DTO Isolation**: Entity classes are never exposed directly to HTTP request or response payloads. All exchanges utilize validated request DTOs (`@Valid`, Jakarta Bean Validation) and strongly typed response DTOs wrapped in a uniform `ApiResponse<T>`.
- **Global Exception Handling**: Centralized `@RestControllerAdvice` intercepts `MethodArgumentNotValidException`, `ResourceNotFoundException`, `AccessDeniedException`, `BadCredentialsException`, `DuplicateResourceException`, and uncaught exceptions, producing sanitized, RFC-7807-inspired JSON problem responses without stack trace leaks.

### 3.3 Domain & Business Service Layer
- **Patient Management**: Handles profile registration, multi-patient ownership per user, updates, and soft/safe deactivation.
- **Wellness Records**: Logs granular health markers: record date, weight (kg), sleep hours, water intake (mL), exercise duration (minutes), mood category, energy level (1–10), stress level (1–10), and subjective notes.
- **Wellness Summary & Analytics**: Computes statistical summaries over custom date ranges, trailing 7 days (weekly), and trailing 30 days (monthly). Computes arithmetic averages, metric completion scores, weight trends, and mood frequency distributions.
- **Wellness Plans**: Establishes actionable lifestyle guidelines (daily water target, daily sleep target, weekly workout duration) and computes active adherence scores against logged wellness records.
- **AI Wellness Insights**: Orchestrates AI analysis while applying stringent safety rules:
  - Minimum necessary data sent (anonymized dates and health metrics only; zero names, emails, or PII).
  - Explicit disclaimers: strictly informational wellness guidance, not medical diagnosis or prescription.
  - Deterministic fallback provider when no API key is configured or remote service is unreachable.

### 3.4 Persistence & Data Migration
- **Spring Data JPA & Hibernate 6**: Optimized queries with indexes on high-cardinality foreign keys (`user_id`, `patient_id`, `record_date`).
- **Flyway Database Migrations**: Versioned SQL scripts (`V1__init_schema.sql`, `V2__seed_default_admin.sql`) ensure consistent, reproducible schemas without relying on `ddl-auto: update` or `create`.

---

## 4. Package Structure

```
com.patientwellness
├── PatientWellnessApplication.java
├── config
│   ├── ApplicationProperties.java
│   ├── OpenApiConfig.java
│   ├── SecurityConfig.java
│   └── WebConfig.java
├── controller
│   ├── AuthController.java
│   ├── PatientController.java
│   ├── WellnessRecordController.java
│   ├── WellnessSummaryController.java
│   ├── WellnessPlanController.java
│   └── WellnessInsightController.java
├── dto
│   ├── request
│   │   ├── AuthLoginRequest.java
│   │   ├── AuthRegisterRequest.java
│   │   ├── PatientCreateRequest.java
│   │   ├── PatientUpdateRequest.java
│   │   ├── WellnessRecordRequest.java
│   │   ├── WellnessPlanRequest.java
│   │   └── WellnessPlanStatusUpdateRequest.java
│   └── response
│       ├── ApiResponse.java
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── PatientResponse.java
│       ├── WellnessRecordResponse.java
│       ├── WellnessSummaryResponse.java
│       ├── WellnessPlanResponse.java
│       ├── WellnessPlanProgressResponse.java
│       ├── WellnessInsightResponse.java
│       └── ErrorResponse.java
├── entity
│   ├── BaseEntity.java
│   ├── User.java
│   ├── Patient.java
│   ├── WellnessRecord.java
│   ├── WellnessPlan.java
│   └── WellnessInsight.java
├── enums
│   ├── Role.java
│   ├── Gender.java
│   ├── Mood.java
│   ├── PlanStatus.java
│   └── InsightStatus.java
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   ├── ForbiddenException.java
│   ├── DuplicateResourceException.java
│   ├── ValidationException.java
│   └── LlmServiceException.java
├── integration
│   └── llm
│       ├── LLMProvider.java
│       ├── PromptBuilder.java
│       ├── OpenAiCompatibleLLMProvider.java
│       ├── DeterministicFallbackLLMProvider.java
│       └── LLMProviderFactory.java
├── repository
│   ├── UserRepository.java
│   ├── PatientRepository.java
│   ├── WellnessRecordRepository.java
│   ├── WellnessPlanRepository.java
│   └── WellnessInsightRepository.java
├── security
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   ├── UserPrincipal.java
│   └── OwnershipValidator.java
├── service
│   ├── AuthService.java
│   ├── PatientService.java
│   ├── WellnessRecordService.java
│   ├── WellnessSummaryService.java
│   ├── WellnessPlanService.java
│   ├── WellnessInsightService.java
│   └── impl
│       ├── AuthServiceImpl.java
│       ├── PatientServiceImpl.java
│       ├── WellnessRecordServiceImpl.java
│       ├── WellnessSummaryServiceImpl.java
│       ├── WellnessPlanServiceImpl.java
│       └── WellnessInsightServiceImpl.java
└── util
    ├── DateTimeUtils.java
    └── StatisticsCalculator.java
```

---

## 5. Security Architecture

1. **Authentication Protocol**: JWT Bearer tokens utilizing HMAC-SHA256 (HS256) or HMAC-SHA512 (HS512). Secret keys are loaded securely from environment variables (`JWT_SECRET`) with a minimum length requirement (32+ characters).
2. **Password Security**: Passwords are hashed using BCrypt with standard cost factor 12. Password hashes are never written to audit logs, API responses, or error payloads.
3. **Data Isolation (Tenant Boundary)**:
   - Every `Patient` record contains an indexed `user_id`.
   - Every query for `Patient`, `WellnessRecord`, `WellnessPlan`, and `WellnessInsight` is scoped to the authenticated user ID unless explicitly executed by an authorized `ROLE_ADMIN` context with deliberate administrative parameters.
4. **CORS & CSRF**:
   - CSRF protection is safely disabled because the API uses stateless JWT authentication without cookies.
   - CORS is strictly configured to permit authorized origins, headers, and HTTP methods (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`).

---

## 6. AI Safety and Data Privacy Protocols

- **De-identification**: The system never passes names, emails, addresses, phone numbers, or user IDs to the LLM. Only synthetic patient identifiers (e.g. `Patient-Session-Alpha`), age group, and numerical wellness metrics are included.
- **Mandatory Guardrails**: Prompts explicitly forbid medical diagnoses, prescription recommendations, and statements implying doctor replacement.
- **Output Validation**: Responses are scanned to verify mandatory presence of the disclaimer header: `"AI Wellness Insights are for educational and motivational purposes only and do not constitute medical advice."`
- **Fallback Guarantee**: In case of network downtime, rate limits, or missing API keys, the system transparently utilizes the deterministic rules-based `DeterministicFallbackLLMProvider`, flagging the response with `provider: "DETERMINISTIC_FALLBACK"`.
