package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskListingGateway;
import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.usecase.task.TaskListingUseCase;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TaskListingUseCaseImpl implements TaskListingUseCase {

    private final TaskListingGateway taskListingGateway;
    private final UserQueryGateway userQueryGateway;

    public TaskListingUseCaseImpl(
            TaskListingGateway taskListingGateway,
            UserQueryGateway userQueryGateway) {
        this.taskListingGateway = taskListingGateway;
        this.userQueryGateway = userQueryGateway;
    }

    @Override
    public List<Task> listByUserId(UUID userId) throws TaskException, UserException {
        var user = userQueryGateway.findById(userId)
                .orElseThrow(() -> new TaskException(
                        ErrorCodeEnum.USER0001.getMessage(),
                        ErrorCodeEnum.USER0001.getCode()
                ));

        return taskListingGateway.listTasksByUserId(userId);
    }

    @Override
    public List<Task> listByTitle(UUID userId, String title) throws TaskException, UserException {
        var user = userQueryGateway.findById(userId)
                .orElseThrow(() -> new TaskException(
                        ErrorCodeEnum.USER0001.getMessage(),
                        ErrorCodeEnum.USER0001.getCode()
                ));

        return taskListingGateway.listTasksByTitle(userId, title);
    }

    @Override
    public List<Task> listByDueDate(UUID userId, LocalDateTime date) throws TaskException , UserException{
        var user = userQueryGateway.findById(userId)
                .orElseThrow(() -> new TaskException(
                        ErrorCodeEnum.USER0001.getMessage(),
                        ErrorCodeEnum.USER0001.getCode()
                ));

        return taskListingGateway.listTasksByDueDate(userId, date);
    }
}