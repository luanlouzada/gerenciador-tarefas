package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskNotificationGateway;
import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.usecase.task.TaskNotificationUseCase;

import java.time.LocalDate;
import java.util.List;

public class TaskNotificationUseCaseImpl implements TaskNotificationUseCase {

    private final TaskNotificationGateway taskNotificationGateway;

    public TaskNotificationUseCaseImpl(TaskNotificationGateway taskNotificationGateway) {
        this.taskNotificationGateway = taskNotificationGateway;
    }

    @Override
    public void notifyTasksDueToday() throws SystemException {
        var tasks = taskNotificationGateway.findTasksDueToday();
        for (var task : tasks) {
            taskNotificationGateway.sendNotification(task);
        }
    }

    @Override
    public void notifyTasksDueOn(LocalDate date) throws SystemException {
        var tasks = taskNotificationGateway.findTasksDueOn(date);
        for (var task : tasks) {
            taskNotificationGateway.sendNotification(task);
        }
    }

    @Override
    public void notifyDueTasks(List<Task> tasks) throws SystemException {
        for (var task : tasks) {
            taskNotificationGateway.sendNotification(task);
        }
    }

    @Override
    public void notifyTasksAboutToExpire() throws SystemException {
        List<Task> tasks60min = taskNotificationGateway.findTasksAboutToExpire(60);
        if (!tasks60min.isEmpty()) {
            for (Task task : tasks60min) {
                taskNotificationGateway.sendNotification(task);
            }
        }

        List<Task> tasks30min = taskNotificationGateway.findTasksAboutToExpire(30);
        if (!tasks30min.isEmpty()) {
            for (Task task : tasks30min) {
                taskNotificationGateway.sendNotification(task);
            }
        }


        List<Task> tasks5min = taskNotificationGateway.findTasksAboutToExpire(5);
        if (!tasks5min.isEmpty()) {
            for (Task task : tasks5min) {
                taskNotificationGateway.sendNotification(task);
            }
        }


        List<Task> tasks1min = taskNotificationGateway.findTasksAboutToExpire(1);
        if (!tasks1min.isEmpty()) {
            for (Task task : tasks1min) {
                taskNotificationGateway.sendNotification(task);
            }
        }
    }
}