package br.com.gerenciador.infrastructure.service.task;

import br.com.gerenciador.application.gateway.task.TaskUpdateGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.entity.TaskEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.TaskEntityRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class TaskUpdateGatewayImpl implements TaskUpdateGateway {

    private final TaskEntityRepository taskEntityRepository;
    private final TaskMapper taskMapper;

    public TaskUpdateGatewayImpl(
            TaskEntityRepository taskEntityRepository,
            TaskMapper taskMapper) {
        this.taskEntityRepository = taskEntityRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public Task updateTask(UUID taskId, Task task) throws TaskException {
        try {
            TaskEntity taskEntity = taskEntityRepository.findById(taskId)
                    .orElseThrow(() -> new TaskException(
                            ErrorCodeEnum.TASK0001.getMessage(),
                            ErrorCodeEnum.TASK0001.getCode()));

            if (taskEntity.getDeletedAt() != null) {
                throw new TaskException(
                        ErrorCodeEnum.TASK0001.getMessage(),
                        ErrorCodeEnum.TASK0001.getCode());
            }

            if (!taskEntity.getUser().getId().equals(task.getUserId())) {
                throw new TaskException(
                        ErrorCodeEnum.TASK0002.getMessage(),
                        ErrorCodeEnum.TASK0002.getCode());
            }

            taskEntity.setTitle(task.getTitle());
            taskEntity.setDescription(task.getDescription());
            taskEntity.setStatus(task.getStatus());
            taskEntity.setDueAt(task.getDueAt());

            TaskEntity updatedEntity = taskEntityRepository.save(taskEntity);
            return taskMapper.toTask(updatedEntity);
        } catch (TaskException e) {
            log.error("Erro ao atualizar tarefa::TaskUpdateGatewayImpl", e);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar tarefa::TaskUpdateGatewayImpl", e);
            throw new TaskException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode());
        }
    }
}