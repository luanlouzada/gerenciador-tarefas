package br.com.gerenciador.application.gateway.task;

import java.time.LocalDate;
import java.util.List;

import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.model.Task;

public interface TaskNotificationGateway {
    List<Task> findTasksDueToday() throws SystemException;

    List<Task> findTasksDueOn(LocalDate date) throws SystemException;

    void sendNotification(Task task) throws SystemException;

    List<Task> findTasksAboutToExpire(int minutes) throws SystemException;
}