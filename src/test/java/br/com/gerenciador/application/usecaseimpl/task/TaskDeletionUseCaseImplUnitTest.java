package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskDeletionGateway;
import br.com.gerenciador.application.gateway.task.TaskQueryGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskDeletionUseCaseImplUnitTest {

    @Mock
    private TaskDeletionGateway taskDeletionGateway;

    @Mock
    private TaskQueryGateway taskQueryGateway;

    @InjectMocks
    private TaskDeletionUseCaseImpl useCase;

    @Test
    void givenTaskNotOwnedByUser_whenDelete_thenThrowsTaskException() throws TaskException {
        // Given user is not owner of the task
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(taskQueryGateway.isTaskOwnedByUser(userId, taskId)).thenReturn(false);

        // When + Then: expect TaskException with code TASK0002
        TaskException ex = assertThrows(TaskException.class, () -> useCase.delete(userId, taskId));
        assert ex.getCode().equals(ErrorCodeEnum.TASK0002.getCode());
        assert ex.getMessage().equals(ErrorCodeEnum.TASK0002.getMessage());

        verify(taskQueryGateway).isTaskOwnedByUser(userId, taskId);
        verifyNoInteractions(taskDeletionGateway);
    }

    @Test
    void givenTaskOwnedByUser_whenDelete_thenDeletesTask() throws TaskException {
        // Given user owns the task
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(taskQueryGateway.isTaskOwnedByUser(userId, taskId)).thenReturn(true);

        // When
        useCase.delete(userId, taskId);

        // Then: verify deletion gateway invoked
        verify(taskQueryGateway).isTaskOwnedByUser(userId, taskId);
        verify(taskDeletionGateway).deleteTask(userId, taskId);
    }
}
