package br.com.gerenciador.infrastructure.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for user login")
public record LoginUserRequest(
        @Schema(description = "User email address", example = "user@example.com") @NotBlank String email,

        @Schema(description = "User password", example = "Password123!") @NotBlank String password) {

}