package br.com.gerenciador.usecase.task;

import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.model.Task;

import java.time.LocalDate;
import java.util.List;

public interface TaskNotificationUseCase {
    void notifyTasksDueToday() throws SystemException;

    void notifyTasksDueOn(LocalDate date) throws SystemException;

    void notifyDueTasks(List<Task> tasks) throws SystemException;
}