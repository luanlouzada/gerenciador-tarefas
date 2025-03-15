package br.com.gerenciador.application.gateway.task;

import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.model.Task;

import java.util.List;

public interface TaskSchedulerGateway {
    void checkDueTasks(List<Task> tasks) throws SystemException;

    void checkExpiredTasks(List<Task> tasks) throws SystemException;
}
