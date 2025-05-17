package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskSchedulerGateway;
import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskSchedulerUseCaseImplUnitTest {

    @Mock
    private TaskSchedulerGateway taskSchedulerGateway;

    @InjectMocks
    private TaskSchedulerUseCaseImpl useCase;

    @Test
    void givenTasks_whenCheckDueTasks_thenDelegatesToGateway() throws SystemException {
        // Given: a list of tasks to check
        Task t1 = new Task();
        Task t2 = new Task();
        List<Task> tasks = List.of(t1, t2);

        // When
        useCase.checkDueTasks(tasks);

        // Then: verify delegation
        verify(taskSchedulerGateway).checkDueTasks(tasks);
        verifyNoMoreInteractions(taskSchedulerGateway);
    }

    @Test
    void givenGatewayThrowsException_whenCheckDueTasks_thenThrowsSystemException() throws SystemException {
        // Given
        List<Task> tasks = List.of(new Task());
        doThrow(new SystemException("gateway failure", "SYS002"))
                .when(taskSchedulerGateway).checkDueTasks(tasks);

        // When + Then: expect SystemException
        assertThrows(SystemException.class, () -> useCase.checkDueTasks(tasks));
        verify(taskSchedulerGateway).checkDueTasks(tasks);
    }

    @Test
    void givenTasks_whenCheckExpiredTasks_thenDelegatesToGateway() throws SystemException {
        // Given: tasks to check expired
        Task t = new Task();
        List<Task> tasks = List.of(t);

        // When
        useCase.checkExpiredTasks(tasks);

        // Then: verify delegation
        verify(taskSchedulerGateway).checkExpiredTasks(tasks);
        verifyNoMoreInteractions(taskSchedulerGateway);
    }

    @Test
    void givenGatewayThrowsException_whenCheckExpiredTasks_thenThrowsSystemException() throws SystemException {
        // Given
        List<Task> tasks = List.of(new Task());
        doThrow(new SystemException("expired failure", "SYS003"))
                .when(taskSchedulerGateway).checkExpiredTasks(tasks);

        // When + Then
        assertThrows(SystemException.class, () -> useCase.checkExpiredTasks(tasks));
        verify(taskSchedulerGateway).checkExpiredTasks(tasks);
    }
}
