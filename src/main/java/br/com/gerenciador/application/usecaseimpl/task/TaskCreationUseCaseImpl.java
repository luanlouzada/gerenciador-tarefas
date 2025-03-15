package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskCreationGateway;
import br.com.gerenciador.application.gateway.task.TaskQueryGateway;
import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.usecase.task.TaskCreationUseCase;

import java.time.LocalDateTime;

public class TaskCreationUseCaseImpl implements TaskCreationUseCase {

    private final TaskCreationGateway taskCreationGateway;
    private final UserQueryGateway userQueryGateway;
    private final TaskQueryGateway taskQueryGateway;

    public TaskCreationUseCaseImpl(
            TaskCreationGateway taskCreationGateway,
            UserQueryGateway userQueryGateway,
            TaskQueryGateway taskQueryGateway) {
        this.taskCreationGateway = taskCreationGateway;
        this.userQueryGateway = userQueryGateway;
        this.taskQueryGateway = taskQueryGateway;
    }

    @Override
    public Task createTask(Task task) throws TaskException, UserException {
        var user = userQueryGateway.findById(task.getUserId())
                .orElseThrow(() -> new TaskException(
                        ErrorCodeEnum.USER0001.getMessage(),
                        ErrorCodeEnum.USER0001.getCode()
                ));

        if (taskQueryGateway.existsByTitleAndUserId(task.getUserId(), task.getTitle())) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0008.getMessage(),
                    ErrorCodeEnum.TASK0008.getCode()
            );
        }

        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }

        try {
            return taskCreationGateway.createTask(task);
        } catch (Exception e) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0003.getMessage(),
                    ErrorCodeEnum.TASK0003.getCode()
            );
        }
    }
}