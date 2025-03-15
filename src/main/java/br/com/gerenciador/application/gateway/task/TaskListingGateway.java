package br.com.gerenciador.application.gateway.task;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.model.Task;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskListingGateway {
    List<Task> listTasksByUserId(UUID userId) throws TaskException;
    List<Task> listTasksByTitle(UUID userId, String title) throws TaskException;
    List<Task> listTasksByDueDate(UUID userId, LocalDateTime date) throws TaskException;
}