package br.com.gerenciador.infrastructure.dto.request.user;


import jakarta.validation.constraints.NotBlank;


public record RegistrationUserRequest(@NotBlank String email, @NotBlank String name, @NotBlank String password) {

}