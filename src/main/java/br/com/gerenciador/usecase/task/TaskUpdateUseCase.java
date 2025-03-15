package br.com.gerenciador.usecase.task;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.model.Task;

import java.util.UUID;

public interface TaskUpdateUseCase {
    Task update(UUID taskId, Task taskUpdateData) throws TaskException;
}