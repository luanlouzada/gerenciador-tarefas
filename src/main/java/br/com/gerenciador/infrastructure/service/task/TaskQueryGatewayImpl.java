package br.com.gerenciador.infrastructure.service.task;

import br.com.gerenciador.application.gateway.task.TaskQueryGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.entity.TaskEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.TaskEntityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class TaskQueryGatewayImpl implements TaskQueryGateway {

    private final TaskEntityRepository taskEntityRepository;
    private final TaskMapper taskMapper;

    public TaskQueryGatewayImpl(
            TaskEntityRepository taskEntityRepository,
            TaskMapper taskMapper) {
        this.taskEntityRepository = taskEntityRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public Optional<Task> findTaskById(UUID taskId) throws TaskException {
        try {
            Optional<TaskEntity> taskEntityOptional = taskEntityRepository.findById(taskId);
            if (taskEntityOptional.isEmpty() || taskEntityOptional.get().getDeletedAt() != null) {
                return Optional.empty();
            }

            Task task = taskMapper.toTask(taskEntityOptional.get());
            return Optional.of(task);
        } catch (Exception e) {
            log.error("Erro ao buscar tarefa por ID::TaskQueryGatewayImpl", e);
            throw new TaskException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode());
        }
    }

    @Override
    public boolean isTaskOwnedByUser(UUID userId, UUID taskId) throws TaskException {
        try {
            Optional<TaskEntity> taskEntityOptional = taskEntityRepository.findById(taskId);
            if (taskEntityOptional.isEmpty() || taskEntityOptional.get().getDeletedAt() != null) {
                return false;
            }

            return taskEntityOptional.get().getUser().getId().equals(userId);
        } catch (Exception e) {
            log.error("Erro ao verificar propriedade da tarefa::TaskQueryGatewayImpl", e);
            throw new TaskException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode());
        }
    }

    @Override
    public boolean existsByTitleAndUserId(UUID userId, String title) {
        return taskEntityRepository.existsByUserIdAndTitleIgnoreCaseAndDeletedAtIsNull(userId, title);
    }
}