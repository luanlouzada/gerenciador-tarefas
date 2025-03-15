package br.com.gerenciador.infrastructure.service.task;

import br.com.gerenciador.application.gateway.task.TaskDeletionGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.entity.TaskEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.TaskEntityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class TaskDeletionGatewayImpl implements TaskDeletionGateway {

    private final TaskEntityRepository taskEntityRepository;
    private final TaskMapper taskMapper;

    public TaskDeletionGatewayImpl(
            TaskEntityRepository taskEntityRepository,
            TaskMapper taskMapper) {
        this.taskEntityRepository = taskEntityRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public void deleteTask(UUID userId, UUID taskId) throws TaskException {
        try {
            TaskEntity taskEntity = taskEntityRepository.findById(taskId)
                    .orElseThrow(() -> new TaskException(
                            ErrorCodeEnum.TASK0001.getMessage(),
                            ErrorCodeEnum.TASK0001.getCode()));

            Task task = taskMapper.toTask(taskEntity);

            if (!task.getUserId().equals(userId)) {
                throw new TaskException(
                        ErrorCodeEnum.TASK0002.getMessage(),
                        ErrorCodeEnum.TASK0002.getCode());
            }

            taskEntity.setDeletedAt(LocalDateTime.now());
            taskEntityRepository.save(taskEntity);
        } catch (TaskException e) {
            log.error("Erro ao excluir tarefa::TaskDeletionGatewayImpl", e);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao excluir tarefa::TaskDeletionGatewayImpl", e);
            throw new TaskException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode());
        }
    }
}