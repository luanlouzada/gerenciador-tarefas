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
}