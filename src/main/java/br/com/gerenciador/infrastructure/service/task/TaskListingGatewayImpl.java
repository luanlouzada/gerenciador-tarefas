package br.com.gerenciador.infrastructure.service.task;

import br.com.gerenciador.application.gateway.task.TaskListingGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.entity.TaskEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.TaskEntityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class TaskListingGatewayImpl implements TaskListingGateway {

    private final TaskMapper taskMapper;
    private final TaskEntityRepository taskEntityRepository;

    public TaskListingGatewayImpl(
            TaskEntityRepository taskEntityRepository,
            TaskMapper taskMapper) {
        this.taskEntityRepository = taskEntityRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public List<Task> listTasksByUserId(UUID userId) throws TaskException {
        try {
            List<TaskEntity> taskEntities = taskEntityRepository.findByUserIdAndDeletedAtIsNull(userId);

            LocalDateTime now = LocalDateTime.now();
            List<TaskEntity> validTasks = taskEntities.stream()
                    .filter(entity -> entity.getDueAt().isAfter(now) || entity.getDueAt().isEqual(now))
                    .collect(Collectors.toList());

            return convertToTaskList(validTasks, "listTasksByUserId");
        } catch (Exception e) {
            log.error("Erro ao listar tarefas por usuário::TaskListingGatewayImpl", e);
            throw new TaskException("Erro interno do servidor", ErrorCodeEnum.SYS0001.getCode());
        }
    }

    @Override
    public List<Task> listTasksByTitle(UUID userId, String title) throws TaskException {
        try {
            List<TaskEntity> taskEntities = taskEntityRepository.findByUserIdAndTitleContainingAndDeletedAtIsNull(userId, title);
            return convertToTaskList(taskEntities, "listTasksByTitle");
        } catch (Exception e) {
            log.error("Erro ao listar tarefas por título::TaskListingGatewayImpl", e);
            throw new TaskException("Erro interno do servidor", ErrorCodeEnum.SYS0001.getCode());
        }
    }

    @Override
    public List<Task> listTasksByDueDate(UUID userId, LocalDateTime date) throws TaskException {
        try {
            List<TaskEntity> taskEntities = taskEntityRepository.findByUserIdAndDueAtAndDeletedAtIsNull(userId, date);
            return convertToTaskList(taskEntities, "listTasksByDueDate");
        } catch (Exception e) {
            log.error("Erro ao listar tarefas por data de vencimento::TaskListingGatewayImpl", e);
            throw new TaskException("Data de vencimento inválida", ErrorCodeEnum.TASK0004.getCode());
        }
    }

    private List<Task> convertToTaskList(List<TaskEntity> entities, String operationName) {
        return entities.stream()
                .map(entity -> {
                    try {
                        return taskMapper.toTask(entity);
                    } catch (TaskException e) {
                        log.error("Erro ao converter entidade de tarefa::", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}