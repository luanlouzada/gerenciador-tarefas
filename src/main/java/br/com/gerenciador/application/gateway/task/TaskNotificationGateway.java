package br.com.gerenciador.application.gateway.task;

import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.model.Task;

import java.time.LocalDate;
import java.util.List;

public interface TaskNotificationGateway {
    List<Task> findTasksDueToday() throws SystemException;

    List<Task> findTasksDueOn(LocalDate date) throws SystemException;

    void sendNotification(Task task) throws SystemException;


}