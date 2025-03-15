package br.com.gerenciador.infrastructure.dto.request.task;

import br.com.gerenciador.domain.enums.TaskStatusEnum;

import java.time.LocalDateTime;

public record TaskFilterRequest(
        String title,
        TaskStatusEnum status,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}