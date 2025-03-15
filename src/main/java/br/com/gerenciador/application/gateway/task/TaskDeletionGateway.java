package br.com.gerenciador.application.gateway.task;

import br.com.gerenciador.domain.exception.TaskException;

import java.util.UUID;

public interface TaskDeletionGateway {
    void deleteTask(UUID userId, UUID taskId) throws TaskException;
}