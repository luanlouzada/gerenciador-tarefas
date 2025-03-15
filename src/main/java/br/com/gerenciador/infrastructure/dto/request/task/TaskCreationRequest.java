package br.com.gerenciador.infrastructure.dto.request.task;

import jakarta.validation.constraints.NotBlank;

public record TaskCreationRequest(@NotBlank String title, @NotBlank String description, @NotBlank String dueAt) {
}
