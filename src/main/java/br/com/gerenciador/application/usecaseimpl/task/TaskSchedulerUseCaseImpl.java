package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskSchedulerGateway;
import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.usecase.task.TaskSchedulerUseCase;

import java.util.List;

public class TaskSchedulerUseCaseImpl implements TaskSchedulerUseCase {
    private final TaskSchedulerGateway taskSchedulerGateway;

    public TaskSchedulerUseCaseImpl(TaskSchedulerGateway taskSchedulerGateway) {
        this.taskSchedulerGateway = taskSchedulerGateway;
    }

    @Override
    public void checkDueTasks(List<Task> tasks) throws SystemException {
        taskSchedulerGateway.checkDueTasks(tasks);
    }

    @Override
    public void checkExpiredTasks(List<Task> tasks) throws SystemException {
        taskSchedulerGateway.checkExpiredTasks(tasks);
    }
}