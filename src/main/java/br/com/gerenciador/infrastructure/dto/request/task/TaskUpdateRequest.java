package br.com.gerenciador.infrastructure.dto.request.task;

import br.com.gerenciador.domain.enums.TaskStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskUpdateRequest(
        @NotBlank String title,
        String description,
        @NotNull TaskStatusEnum status,
        @NotNull LocalDateTime dueAt
) {
}