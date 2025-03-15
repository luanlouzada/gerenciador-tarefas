package br.com.gerenciador.usecase.task;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.Task;

public interface TaskCreationUseCase {
    Task createTask(Task task) throws TaskException, UserException;
}