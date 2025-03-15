package br.com.gerenciador.infrastructure.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(@NotBlank String email, @NotBlank String password) {

}