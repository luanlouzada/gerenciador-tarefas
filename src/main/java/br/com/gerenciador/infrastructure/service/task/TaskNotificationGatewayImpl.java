package br.com.gerenciador.infrastructure.service.task;

import br.com.gerenciador.application.gateway.task.TaskNotificationGateway;
import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.entity.TaskEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.TaskEntityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class TaskNotificationGatewayImpl implements TaskNotificationGateway {

    private final TaskEntityRepository taskEntityRepository;
    private final TaskMapper taskMapper;

    public TaskNotificationGatewayImpl(
            TaskEntityRepository taskEntityRepository,
            TaskMapper taskMapper) {
        this.taskEntityRepository = taskEntityRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public List<Task> findTasksDueToday() throws SystemException {
        try {
            LocalDate today = LocalDate.now();
            return findTasksDueOn(today);
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar tarefas do dia::TaskNotificationGatewayImpl", e);
            throw new SystemException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode());
        }
    }

    @Override
    public List<Task> findTasksDueOn(LocalDate date) throws SystemException {
        try {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<TaskEntity> taskEntities =
                    taskEntityRepository.findByDueAtBetweenAndDeletedAtIsNull(startOfDay, endOfDay);

            return taskEntities.stream()
                    .map(entity -> {
                        try {
                            return taskMapper.toTask(entity);
                        } catch (TaskException e) {
                            log.error("Erro ao converter entidade de tarefa::findTasksDueOn", e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao buscar tarefas por data::TaskNotificationGatewayImpl", e);
            throw new SystemException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode());
        }
    }

    @Override
    public void sendNotification(Task task) throws SystemException {
        try {

            log.info("Enviando notificação para a tarefa: " + task.getTitle());

            // TODO: Lógica de implementação da notificação

        } catch (Exception e) {
            log.error("Erro ao enviar notificação::TaskNotificationGatewayImpl", e);
            throw new SystemException(
                    ErrorCodeEnum.SYS0003.getMessage(),
                    ErrorCodeEnum.SYS0003.getCode());
        }
    }
}