package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskDeletionGateway;
import br.com.gerenciador.application.gateway.task.TaskQueryGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.usecase.task.TaskDeletionUseCase;

import java.util.UUID;

public class TaskDeletionUseCaseImpl implements TaskDeletionUseCase {

    private final TaskDeletionGateway taskDeletionGateway;
    private final TaskQueryGateway taskQueryGateway;

    public TaskDeletionUseCaseImpl(
            TaskDeletionGateway taskDeletionGateway,
            TaskQueryGateway taskQueryGateway) {
        this.taskDeletionGateway = taskDeletionGateway;
        this.taskQueryGateway = taskQueryGateway;
    }

    @Override
    public void delete(UUID userId, UUID taskId) throws TaskException {
        boolean isOwner = taskQueryGateway.isTaskOwnedByUser(userId, taskId);
        if (!isOwner) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0002.getMessage(),
                    ErrorCodeEnum.TASK0002.getCode()
            );
        }

        taskDeletionGateway.deleteTask(userId, taskId);
    }
}