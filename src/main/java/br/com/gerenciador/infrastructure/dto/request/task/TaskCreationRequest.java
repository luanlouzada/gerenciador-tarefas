package br.com.gerenciador.infrastructure.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TaskCreationRequest(
        @Schema(description = "Task title (minimum 20 characters)", example = "Complete the project documentation") @NotBlank String title,

        @Schema(description = "Task description", example = "Write all the required documentation for the project") @NotBlank String description,

        @Schema(description = "Task due date in ISO format (yyyy-MM-dd'T'HH:mm:ss)", example = "2023-12-31T23:59:59") @NotBlank String dueAt) {
}
