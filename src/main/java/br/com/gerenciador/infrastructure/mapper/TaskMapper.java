package br.com.gerenciador.infrastructure.mapper;

import br.com.gerenciador.domain.enums.TaskStatusEnum;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.dto.request.task.TaskCreationRequest;
import br.com.gerenciador.infrastructure.dto.request.task.TaskUpdateRequest;
import br.com.gerenciador.infrastructure.entity.TaskEntity;
import br.com.gerenciador.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class TaskMapper {

    private final UserMapper userMapper;

    public TaskMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public TaskEntity toTaskEntity(Task task, UserEntity userEntity) {
        return new TaskEntity(
                task.getId(),
                userEntity,
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getDeletedAt(),
                task.getDueAt()
        );
    }

    public Task toTask(TaskEntity taskEntity) throws TaskException {
        return new Task(
                taskEntity.getUser() != null ? taskEntity.getUser().getId() : null,
                taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getStatus(),
                taskEntity.getCreatedAt(),
                taskEntity.getDueAt()
        );
    }

    public Task toTask(TaskCreationRequest request, UserEntity userEntity) throws TaskException {
        LocalDateTime dueAtDate;

        if (request.dueAt() != null && !request.dueAt().isEmpty()) {
            dueAtDate = parseDueDate(request.dueAt());
        } else {
            dueAtDate = LocalDateTime.now().plusDays(1);
            // TODO: remover no futuro se eu realmente obrigar a receber dueAtDate
        }

        return new Task(
                userEntity.getId(),
                null,
                request.title(),
                request.description(),
                TaskStatusEnum.IN_PROGRESS,
                LocalDateTime.now(),
                dueAtDate
        );
    }

    public Task toTask(TaskUpdateRequest request, UserEntity userEntity) throws TaskException {
        LocalDateTime dueAtDate;

        if (request.dueAt() != null && !request.dueAt().isEmpty()) {
            dueAtDate = parseDueDate(request.dueAt());
        } else {
            dueAtDate = LocalDateTime.now().plusDays(1);
        }

        Task task = new Task(
                userEntity.getId(),
                null,
                request.title(),
                request.description(),
                request.status() != null ? request.status() : TaskStatusEnum.IN_PROGRESS,
                LocalDateTime.now(),
                dueAtDate
        );

        return task;
    }

    private LocalDateTime parseDueDate(String dueDateStr) throws TaskException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return LocalDateTime.parse(dueDateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0004.getMessage(),
                    ErrorCodeEnum.TASK0004.getCode()
            );
        }
    }


}