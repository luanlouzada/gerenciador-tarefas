package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskQueryGateway;
import br.com.gerenciador.application.gateway.task.TaskUpdateGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskUpdateUseCaseImplUnitTest {

    @Mock
    private TaskUpdateGateway taskUpdateGateway;

    @Mock
    private TaskQueryGateway taskQueryGateway;

    @InjectMocks
    private TaskUpdateUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private UUID taskId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        reset(taskUpdateGateway, taskQueryGateway);
    }

    @Test
    void givenTaskNotFound_whenUpdate_thenThrowsTaskException() throws TaskException {
        // Given: task does not exist
        when(taskQueryGateway.findTaskById(taskId)).thenReturn(Optional.empty());
        Task updateData = new Task();
        updateData.setUserId(ownerId);

        // When + Then: expect TASK0001
        TaskException ex = assertThrows(TaskException.class, () -> useCase.update(taskId, updateData));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.TASK0001.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.TASK0001.getMessage());

        verify(taskQueryGateway).findTaskById(taskId);
        verifyNoInteractions(taskUpdateGateway);
    }

    @Test
    void givenNotOwner_whenUpdate_thenThrowsTaskException() throws TaskException {
        // Given: existing task but user not owner
        Task existing = new Task();
        existing.setUserId(UUID.randomUUID());
        when(taskQueryGateway.findTaskById(taskId)).thenReturn(Optional.of(existing));
        Task updateData = new Task();
        updateData.setUserId(ownerId);
        when(taskQueryGateway.isTaskOwnedByUser(ownerId, taskId)).thenReturn(false);

        // When + Then: expect TASK0002
        TaskException ex = assertThrows(TaskException.class, () -> useCase.update(taskId, updateData));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.TASK0002.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.TASK0002.getMessage());

        verify(taskQueryGateway).findTaskById(taskId);
        verify(taskQueryGateway).isTaskOwnedByUser(ownerId, taskId);
        verifyNoInteractions(taskUpdateGateway);
    }

    @Test
    void givenValidUpdate_whenUpdate_thenSetsUpdatedAtAndDelegatesToGateway() throws TaskException {
        // Given: existing task and owner
        Task existing = new Task();
        existing.setUserId(ownerId);
        when(taskQueryGateway.findTaskById(taskId)).thenReturn(Optional.of(existing));
        when(taskQueryGateway.isTaskOwnedByUser(ownerId, taskId)).thenReturn(true);
        Task updateData = new Task();
        updateData.setUserId(ownerId);
        Task returned = new Task();
        returned.setUserId(ownerId);
        when(taskUpdateGateway.updateTask(eq(taskId), any(Task.class))).thenReturn(returned);

        // When
        Task result = useCase.update(taskId, updateData);

        // Then: verify updatedAt set and delegation
        verify(taskQueryGateway).findTaskById(taskId);
        verify(taskQueryGateway).isTaskOwnedByUser(ownerId, taskId);
        verify(taskUpdateGateway).updateTask(eq(taskId), taskCaptor.capture());
        Task captured = taskCaptor.getValue();
        assertThat(captured.getUpdatedAt()).isNotNull();
        assertThat(captured.getUserId()).isEqualTo(ownerId);
        assertThat(result).isSameAs(returned);
    }
}
