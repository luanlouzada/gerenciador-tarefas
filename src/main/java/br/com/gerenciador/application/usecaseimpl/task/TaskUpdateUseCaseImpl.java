package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskQueryGateway;
import br.com.gerenciador.application.gateway.task.TaskUpdateGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.usecase.task.TaskUpdateUseCase;
import java.time.LocalDateTime;
import java.util.UUID;

public class TaskUpdateUseCaseImpl implements TaskUpdateUseCase {

    private final TaskUpdateGateway taskUpdateGateway;
    private final TaskQueryGateway taskQueryGateway;

    public TaskUpdateUseCaseImpl(
            TaskUpdateGateway taskUpdateGateway,
            TaskQueryGateway taskQueryGateway) {
        this.taskUpdateGateway = taskUpdateGateway;
        this.taskQueryGateway = taskQueryGateway;
    }

    @Override
    public Task update(UUID taskId, Task taskUpdateData) throws TaskException {
        var existingTask = taskQueryGateway.findTaskById(taskId)
                .orElseThrow(() -> new TaskException(
                        ErrorCodeEnum.TASK0001.getMessage(),
                        ErrorCodeEnum.TASK0001.getCode()
                ));

        boolean isOwner = taskQueryGateway.isTaskOwnedByUser(taskUpdateData.getUserId(), taskId);
        if (!isOwner) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0002.getMessage(),
                    ErrorCodeEnum.TASK0002.getCode()
            );
        }

        taskUpdateData.setUpdatedAt(LocalDateTime.now());
        return taskUpdateGateway.updateTask(taskId, taskUpdateData);
    }
}