package br.com.gerenciador.usecase.task;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.Task;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskListingUseCase {
    List<Task> listByUserId(UUID userId) throws TaskException, UserException;
    List<Task> listByTitle(UUID userId, String title) throws TaskException, UserException;
    List<Task> listByDueDate(UUID userId, LocalDateTime date) throws TaskException, UserException;
}