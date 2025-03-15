package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskQueryGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.usecase.task.TaskQueryUseCase;
import java.util.Optional;
import java.util.UUID;

public class TaskQueryUseCaseImpl implements TaskQueryUseCase {

    private final TaskQueryGateway taskQueryGateway;

    public TaskQueryUseCaseImpl(TaskQueryGateway taskQueryGateway) {
        this.taskQueryGateway = taskQueryGateway;
    }

    @Override
    public Optional<Task> findById(UUID taskId) throws TaskException {
        return taskQueryGateway.findTaskById(taskId);
    }
}