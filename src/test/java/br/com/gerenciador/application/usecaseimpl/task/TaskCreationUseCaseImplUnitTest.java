package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskCreationGateway;
import br.com.gerenciador.application.gateway.task.TaskQueryGateway;
import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.domain.model.User;
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
class TaskCreationUseCaseImplUnitTest {

    @Mock
    private TaskCreationGateway taskCreationGateway;

    @Mock
    private UserQueryGateway userQueryGateway;

    @Mock
    private TaskQueryGateway taskQueryGateway;

    @InjectMocks
    private TaskCreationUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    @Test
    void givenUserNotFound_whenCreateTask_thenThrowsTaskException() throws UserException, TaskException {
        // Arrange: usuário não existe
        UUID userId = UUID.randomUUID();
        Task input = new Task();
        input.setUserId(userId);
        when(userQueryGateway.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert: espera TaskException com código USER0001
        TaskException ex = assertThrows(TaskException.class, () -> useCase.createTask(input));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.USER0001.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.USER0001.getMessage());

        verify(userQueryGateway).findById(userId);
        verifyNoMoreInteractions(taskQueryGateway, taskCreationGateway);
    }

    @Test
    void givenTaskWithDuplicateTitle_whenCreateTask_thenThrowsTaskException() throws UserException, TaskException {
        // Arrange: usuário existe e já tem tarefa com mesmo título
        String duplicateTitle = "Título duplicado para teste de unidade";
        UUID userId = UUID.randomUUID();
        Task input = new Task();
        input.setUserId(userId);
        input.setTitle(duplicateTitle);
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(new User()));
        when(taskQueryGateway.existsByTitleAndUserId(userId, duplicateTitle)).thenReturn(true);

        // Act + Assert: espera TaskException com código TASK0008
        TaskException ex = assertThrows(TaskException.class, () -> useCase.createTask(input));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.TASK0008.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.TASK0008.getMessage());

        verify(userQueryGateway).findById(userId);
        verify(taskQueryGateway).existsByTitleAndUserId(userId, duplicateTitle);
        verifyNoMoreInteractions(taskCreationGateway);
    }

    @Test
    void givenValidTaskWithoutCreatedAt_whenCreateTask_thenSetsCreatedAtAndReturnsCreatedTask() throws UserException, TaskException {
        // Arrange: usuário existe, título não duplicado, e gateway devolve a tarefa criada
        String validTitle = "Título válido para teste de criação";
        UUID userId = UUID.randomUUID();
        Task input = new Task();
        input.setUserId(userId);
        input.setTitle(validTitle);
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(new User()));
        when(taskQueryGateway.existsByTitleAndUserId(userId, validTitle)).thenReturn(false);
        Task returned = new Task();
        returned.setUserId(userId);
        returned.setTitle(validTitle);
        when(taskCreationGateway.createTask(any())).thenReturn(returned);

        // Act
        Task result = useCase.createTask(input);

        // Assert
        verify(taskCreationGateway).createTask(taskCaptor.capture());
        Task captured = taskCaptor.getValue();
        assertThat(captured.getCreatedAt()).isNotNull();  // createdAt preenchido
        assertThat(captured.getUserId()).isEqualTo(userId);
        assertThat(captured.getTitle()).isEqualTo(validTitle);

        assertThat(result).isSameAs(returned);
    }

    @Test
    void givenGatewayThrowsException_whenCreateTask_thenThrowsTaskException() throws UserException, TaskException {
        // Arrange: simula falha no gateway de criação
        String errorTitle = "Título que causa falha no gateway teste";
        UUID userId = UUID.randomUUID();
        Task input = new Task();
        input.setUserId(userId);
        input.setTitle(errorTitle);
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(new User()));
        when(taskQueryGateway.existsByTitleAndUserId(userId, errorTitle)).thenReturn(false);
        when(taskCreationGateway.createTask(any())).thenThrow(new RuntimeException("fail!"));

        // Act + Assert
        TaskException ex = assertThrows(TaskException.class, () -> useCase.createTask(input));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.TASK0003.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.TASK0003.getMessage());

        verify(taskCreationGateway).createTask(any());
    }
}
