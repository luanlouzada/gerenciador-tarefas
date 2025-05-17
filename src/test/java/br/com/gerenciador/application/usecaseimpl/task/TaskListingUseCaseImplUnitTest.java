package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskListingGateway;
import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskListingUseCaseImplUnitTest {

    @Mock
    private TaskListingGateway taskListingGateway;

    @Mock
    private UserQueryGateway userQueryGateway;

    @InjectMocks
    private TaskListingUseCaseImpl useCase;

    @Test
    void givenUserNotFound_whenListByUserId_thenThrowsTaskException() throws UserException, TaskException {
        // Given user does not exist
        UUID userId = UUID.randomUUID();
        when(userQueryGateway.findById(userId)).thenReturn(Optional.empty());

        // When + Then: expect TaskException USER0001
        TaskException ex = assertThrows(TaskException.class, () -> useCase.listByUserId(userId));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.USER0001.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.USER0001.getMessage());

        verify(userQueryGateway).findById(userId);
        verifyNoInteractions(taskListingGateway);
    }

    @Test
    void givenUserExists_whenListByUserId_thenReturnsTasks() throws UserException, TaskException {
        // Given user exists and tasks found
        UUID userId = UUID.randomUUID();
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(new User()));
        List<Task> expected = List.of(new Task(), new Task());
        when(taskListingGateway.listTasksByUserId(userId)).thenReturn(expected);

        // When
        List<Task> result = useCase.listByUserId(userId);

        // Then: verify and assert
        verify(userQueryGateway).findById(userId);
        verify(taskListingGateway).listTasksByUserId(userId);
        assertThat(result).isSameAs(expected);
    }

    @Test
    void givenUserNotFound_whenListByTitle_thenThrowsTaskException() throws UserException, TaskException {
        // Given user does not exist
        UUID userId = UUID.randomUUID();
        String title = "Pesquisa Title Exemplo";
        when(userQueryGateway.findById(userId)).thenReturn(Optional.empty());

        // When + Then
        TaskException ex = assertThrows(TaskException.class, () -> useCase.listByTitle(userId, title));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.USER0001.getCode());

        verify(userQueryGateway).findById(userId);
        verifyNoInteractions(taskListingGateway);
    }

    @Test
    void givenUserExists_whenListByTitle_thenReturnsTasks() throws UserException, TaskException {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "Pesquisa Title Exemplo";
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(new User()));
        List<Task> expected = List.of(new Task());
        when(taskListingGateway.listTasksByTitle(userId, title)).thenReturn(expected);

        // When
        List<Task> result = useCase.listByTitle(userId, title);

        // Then
        verify(userQueryGateway).findById(userId);
        verify(taskListingGateway).listTasksByTitle(userId, title);
        assertThat(result).isSameAs(expected);
    }

    @Test
    void givenUserNotFound_whenListByDueDate_thenThrowsTaskException() throws UserException, TaskException {
        // Given
        UUID userId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();
        when(userQueryGateway.findById(userId)).thenReturn(Optional.empty());

        // When + Then
        TaskException ex = assertThrows(TaskException.class, () -> useCase.listByDueDate(userId, date));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.USER0001.getCode());

        verify(userQueryGateway).findById(userId);
        verifyNoInteractions(taskListingGateway);
    }

    @Test
    void givenUserExists_whenListByDueDate_thenReturnsTasks() throws UserException, TaskException {
        // Given
        UUID userId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(new User()));
        List<Task> expected = List.of(new Task());
        when(taskListingGateway.listTasksByDueDate(userId, date)).thenReturn(expected);

        // When
        List<Task> result = useCase.listByDueDate(userId, date);

        // Then
        verify(userQueryGateway).findById(userId);
        verify(taskListingGateway).listTasksByDueDate(userId, date);
        assertThat(result).isSameAs(expected);
    }
}
