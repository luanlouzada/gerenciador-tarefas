package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskQueryGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskQueryUseCaseImplUnitTest {

    @Mock
    private TaskQueryGateway taskQueryGateway;

    @InjectMocks
    private TaskQueryUseCaseImpl useCase;

    @Test
    void givenGatewayThrowsException_whenFindById_thenThrowsTaskException() throws TaskException {
        // Given: gateway throws TaskException
        UUID taskId = UUID.randomUUID();
        when(taskQueryGateway.findTaskById(taskId)).thenThrow(new TaskException("fail to query", "TASK001"));

        // When + Then: expect TaskException
        assertThrows(TaskException.class, () -> useCase.findById(taskId));
        verify(taskQueryGateway).findTaskById(taskId);
    }

    @Test
    void givenTaskNotFound_whenFindById_thenReturnsEmptyOptional() throws TaskException {
        // Given: no task found
        UUID taskId = UUID.randomUUID();
        when(taskQueryGateway.findTaskById(taskId)).thenReturn(Optional.empty());

        // When
        Optional<Task> result = useCase.findById(taskId);

        // Then: result is empty
        assertThat(result).isEmpty();
        verify(taskQueryGateway).findTaskById(taskId);
    }

    @Test
    void givenTaskFound_whenFindById_thenReturnsOptionalWithTask() throws TaskException {
        // Given: task exists
        UUID taskId = UUID.randomUUID();
        Task expected = new Task();
        when(taskQueryGateway.findTaskById(taskId)).thenReturn(Optional.of(expected));

        // When
        Optional<Task> result = useCase.findById(taskId);

        // Then: result contains the task
        assertThat(result).isPresent().containsSame(expected);
        verify(taskQueryGateway).findTaskById(taskId);
    }
}
