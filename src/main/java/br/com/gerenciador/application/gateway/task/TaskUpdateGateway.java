package br.com.gerenciador.application.gateway.task;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.model.Task;

import java.util.UUID;

public interface TaskUpdateGateway {
    Task updateTask(UUID taskId, Task task) throws TaskException;
}