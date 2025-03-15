package br.com.gerenciador.infrastructure.service.task;

import br.com.gerenciador.application.gateway.task.TaskSchedulerGateway;
import br.com.gerenciador.domain.enums.TaskStatusEnum;
import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.entity.TaskEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.TaskEntityRepository;
import br.com.gerenciador.usecase.task.TaskNotificationUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class TaskSchedulerGatewayImpl implements TaskSchedulerGateway {

    private final TaskEntityRepository taskEntityRepository;
    private final TaskMapper taskMapper;
    private final TaskNotificationUseCase taskNotificationUseCase;

    public TaskSchedulerGatewayImpl(
            TaskEntityRepository taskEntityRepository,
            TaskMapper taskMapper,
            TaskNotificationUseCase taskNotificationUseCase) {
        this.taskEntityRepository = taskEntityRepository;
        this.taskMapper = taskMapper;
        this.taskNotificationUseCase = taskNotificationUseCase;
    }

    // Método da interface
    @Override
    public void checkDueTasks(List<Task> tasks) throws SystemException {
        try {
            List<Task> tasksToNotify = new ArrayList<>();
            for (Task task : tasks) {
                if (task.getStatus() != TaskStatusEnum.COMPLETED) {
                    tasksToNotify.add(task);
                }
            }

            if (!tasksToNotify.isEmpty()) {
                log.info("Enviando notificações para {} tarefas::TaskSchedulerGatewayImpl", tasksToNotify.size());
                taskNotificationUseCase.notifyDueTasks(tasksToNotify);
            }
        } catch (Exception e) {
            log.error("Erro ao verificar tarefas próximas ao vencimento::TaskSchedulerGatewayImpl", e);
            throw new SystemException(
                    ErrorCodeEnum.SYS0005.getMessage(),
                    e.getMessage());
        }
    }

    // Método agendado
    @Scheduled(fixedRate = 300000) // (300.000ms) = 5min
    public void checkDueTasksScheduled() {
        log.info("Verificando tarefas próximas ao vencimento::TaskSchedulerGatewayImpl");

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tomorrow = now.plusHours(24);

            List<TaskEntity> dueTasks = taskEntityRepository.findByDueAtBetweenAndDeletedAtIsNull(now, tomorrow);

            List<Task> tasksToNotify = new ArrayList<>();
            for (TaskEntity taskEntity : dueTasks) {
                if (taskEntity.getStatus() != TaskStatusEnum.COMPLETED) {
                    try {
                        Task task = taskMapper.toTask(taskEntity);
                        tasksToNotify.add(task);
                    } catch (TaskException e) {
                        log.error("Erro ao converter entidade para modelo::TaskSchedulerGatewayImpl", e);
                    }
                }
            }

            if (!tasksToNotify.isEmpty()) {
                checkDueTasks(tasksToNotify);
            }
        } catch (Exception e) {
            log.error("Erro ao verificar tarefas próximas ao vencimento::TaskSchedulerGatewayImpl", e);
        }
    }

    // Método da interface
    @Override
    public void checkExpiredTasks(List<Task> tasks) throws SystemException {
        try {
            LocalDateTime now = LocalDateTime.now();
            for (Task task : tasks) {
                if (task.getStatus() != TaskStatusEnum.COMPLETED &&
                        task.getStatus() != TaskStatusEnum.EXPIRED &&
                        task.getDueAt().isBefore(now)) {
                    TaskEntity taskEntity = taskEntityRepository.findById(task.getId())
                            .orElse(null);
                    if (taskEntity != null) {
                        taskEntity.setStatus(TaskStatusEnum.EXPIRED);
                        taskEntity.setUpdatedAt(now);
                        taskEntityRepository.save(taskEntity);
                        log.info("Tarefa ID {} marcada como EXPIRED::TaskSchedulerGatewayImpl", task.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Erro ao verificar tarefas vencidas::TaskSchedulerGatewayImpl", e);
            throw new SystemException(
                    ErrorCodeEnum.SYS0005.getMessage(),
                    e.getMessage());
        }
    }

    // Método agendado
    @Scheduled(fixedRate = 300000) // (300.000ms) = 5min
    public void checkExpiredTasksScheduled() {
        log.info("Verificando tarefas vencidas::TaskSchedulerGatewayImpl");

        try {
            updateExpiredTasks();
            log.info("Verificação de tarefas vencidas concluída::TaskSchedulerGatewayImpl");
        } catch (Exception e) {
            log.error("Erro ao verificar tarefas vencidas::TaskSchedulerGatewayImpl", e);
        }
    }

    private void updateExpiredTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<TaskEntity> expiredTasks = taskEntityRepository.findByDueAtBetweenAndDeletedAtIsNull(
                LocalDateTime.now().minusDays(30), now);

        List<Task> tasks = new ArrayList<>();
        for (TaskEntity taskEntity : expiredTasks) {
            try {
                tasks.add(taskMapper.toTask(taskEntity));
            } catch (TaskException e) {
                log.error("Erro ao converter entidade para modelo::TaskSchedulerGatewayImpl", e);
            }
        }

        if (!tasks.isEmpty()) {
            try {
                checkExpiredTasks(tasks);
            } catch (SystemException e) {
                log.error("Erro ao processar tarefas vencidas::TaskSchedulerGatewayImpl", e);
            }
        }
    }
}