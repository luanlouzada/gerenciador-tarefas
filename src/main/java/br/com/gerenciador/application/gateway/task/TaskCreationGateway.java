package br.com.gerenciador.application.gateway.task;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.model.Task;

public interface TaskCreationGateway {
    Task createTask(Task task) throws TaskException;
}