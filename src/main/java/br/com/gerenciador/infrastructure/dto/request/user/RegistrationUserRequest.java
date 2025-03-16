package br.com.gerenciador.infrastructure.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for user registration")
public record RegistrationUserRequest(
        @Schema(description = "User email address", example = "user@example.com") @NotBlank String email,

        @Schema(description = "User's full name", example = "John Doe") @NotBlank String name,

        @Schema(description = "User password (must contain at least 8 characters, one uppercase, one lowercase, one number and one special character)", example = "Password123!") @NotBlank String password) {

}