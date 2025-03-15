package br.com.gerenciador.usecase.task;

import br.com.gerenciador.domain.exception.TaskException;

import java.util.UUID;

public interface TaskDeletionUseCase {
    void delete(UUID userId, UUID taskId) throws TaskException;
}