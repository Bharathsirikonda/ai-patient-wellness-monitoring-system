package com.patientwellness.dto.request;

import com.patientwellness.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegisterRequest {

    @Schema(description = "User's full name", example = "Jane Doe")
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Schema(description = "Valid unique email address", example = "jane.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Schema(description = "Secure password (min 8 chars, at least 1 digit and 1 letter)", example = "SecretPass123!")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Password must contain at least one letter and one number"
    )
    private String password;

    @Schema(description = "User role (USER or ADMIN)", example = "USER")
    private Role role;
}
