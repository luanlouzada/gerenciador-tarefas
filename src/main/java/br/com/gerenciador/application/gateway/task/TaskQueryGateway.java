package br.com.gerenciador.application.gateway.task;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.model.Task;

import java.util.Optional;
import java.util.UUID;

public interface TaskQueryGateway {
    Optional<Task> findTaskById(UUID taskId) throws TaskException;

    boolean isTaskOwnedByUser(UUID userId, UUID taskId) throws TaskException;

    boolean existsByTitleAndUserId(UUID userId, String title);
}