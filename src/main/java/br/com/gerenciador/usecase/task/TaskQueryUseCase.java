package br.com.gerenciador.usecase.task;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.model.Task;

import java.util.Optional;
import java.util.UUID;

public interface TaskQueryUseCase {
    Optional<Task> findById(UUID taskId) throws TaskException;
}