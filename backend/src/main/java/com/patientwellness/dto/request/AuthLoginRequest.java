package com.patientwellness.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginRequest {

    @Schema(description = "User's registered email address", example = "jane.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Schema(description = "User's password", example = "SecretPass123!")
    @NotBlank(message = "Password is required")
    private String password;
}
