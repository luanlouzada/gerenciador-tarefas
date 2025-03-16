package br.com.gerenciador.usecase.task;

import java.time.LocalDate;
import java.util.List;

import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.model.Task;

public interface TaskNotificationUseCase {
    void notifyTasksDueToday() throws SystemException;

    void notifyTasksDueOn(LocalDate date) throws SystemException;

    void notifyDueTasks(List<Task> tasks) throws SystemException;

    void notifyTasksAboutToExpire() throws SystemException;
}