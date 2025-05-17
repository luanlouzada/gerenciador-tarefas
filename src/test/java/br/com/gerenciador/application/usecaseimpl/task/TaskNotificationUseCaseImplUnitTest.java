package br.com.gerenciador.application.usecaseimpl.task;

import br.com.gerenciador.application.gateway.task.TaskNotificationGateway;
import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskNotificationUseCaseImplUnitTest {

    @Mock
    private TaskNotificationGateway taskNotificationGateway;

    @InjectMocks
    private TaskNotificationUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    @BeforeEach
    void setUp() {
        // reset interactions before each
        reset(taskNotificationGateway);
    }

    @Test
    void givenNoTasksDueToday_whenNotifyTasksDueToday_thenNoNotificationsSent() throws SystemException {
        // Given
        when(taskNotificationGateway.findTasksDueToday()).thenReturn(List.of());

        // When
        useCase.notifyTasksDueToday();

        // Then
        verify(taskNotificationGateway).findTasksDueToday();
        verify(taskNotificationGateway, never()).sendNotification(any());
    }

    @Test
    void givenTasksDueToday_whenNotifyTasksDueToday_thenNotificationsSentForEach() throws SystemException {
        // Given
        Task t1 = new Task();
        Task t2 = new Task();
        List<Task> tasks = List.of(t1, t2);
        when(taskNotificationGateway.findTasksDueToday()).thenReturn(tasks);

        // When
        useCase.notifyTasksDueToday();

        // Then
        verify(taskNotificationGateway).findTasksDueToday();
        verify(taskNotificationGateway, times(tasks.size())).sendNotification(taskCaptor.capture());
        List<Task> sent = taskCaptor.getAllValues();
        assertThat(sent).containsExactlyInAnyOrderElementsOf(tasks);
    }

    @Test
    void givenExceptionFromFindDueToday_whenNotifyTasksDueToday_thenThrowsSystemException() throws SystemException {
        // Given
        when(taskNotificationGateway.findTasksDueToday()).thenThrow(new SystemException("fail", "SYS001"));

        // When + Then
        assertThrows(SystemException.class, () -> useCase.notifyTasksDueToday());
    }

    @Test
    void givenNoTasksOnDate_whenNotifyTasksDueOn_thenNoNotificationsSent() throws SystemException {
        // Given
        LocalDate date = LocalDate.now();
        when(taskNotificationGateway.findTasksDueOn(date)).thenReturn(List.of());

        // When
        useCase.notifyTasksDueOn(date);

        // Then
        verify(taskNotificationGateway).findTasksDueOn(date);
        verify(taskNotificationGateway, never()).sendNotification(any());
    }

    @Test
    void givenTasksOnDate_whenNotifyTasksDueOn_thenNotificationsSentForEach() throws SystemException {
        // Given
        LocalDate date = LocalDate.now();
        Task t = new Task();
        List<Task> tasks = List.of(t);
        when(taskNotificationGateway.findTasksDueOn(date)).thenReturn(tasks);

        // When
        useCase.notifyTasksDueOn(date);

        // Then
        verify(taskNotificationGateway).findTasksDueOn(date);
        verify(taskNotificationGateway, times(tasks.size())).sendNotification(taskCaptor.capture());
        assertThat(taskCaptor.getAllValues()).containsExactlyInAnyOrderElementsOf(tasks);
    }

    @Test
    void givenEmptyList_whenNotifyDueTasks_thenNoNotificationsSent() throws SystemException {
        // Given
        List<Task> tasks = List.of();

        // When
        useCase.notifyDueTasks(tasks);

        // Then
        verify(taskNotificationGateway, never()).sendNotification(any());
    }

    @Test
    void givenTasksList_whenNotifyDueTasks_thenNotificationsSentForEach() throws SystemException {
        // Given
        Task t1 = new Task();
        Task t2 = new Task();
        List<Task> tasks = List.of(t1, t2);

        // When
        useCase.notifyDueTasks(tasks);

        // Then
        verify(taskNotificationGateway, times(tasks.size())).sendNotification(taskCaptor.capture());
        assertThat(taskCaptor.getAllValues()).containsExactlyInAnyOrderElementsOf(tasks);
    }

    @Test
    void givenNoTasksAboutToExpire_whenNotifyTasksAboutToExpire_thenNoNotificationsSent() throws SystemException {
        // Given
        when(taskNotificationGateway.findTasksAboutToExpire(60)).thenReturn(List.of());
        when(taskNotificationGateway.findTasksAboutToExpire(30)).thenReturn(List.of());
        when(taskNotificationGateway.findTasksAboutToExpire(5)).thenReturn(List.of());
        when(taskNotificationGateway.findTasksAboutToExpire(1)).thenReturn(List.of());

        // When
        useCase.notifyTasksAboutToExpire();

        // Then
        verify(taskNotificationGateway).findTasksAboutToExpire(60);
        verify(taskNotificationGateway).findTasksAboutToExpire(30);
        verify(taskNotificationGateway).findTasksAboutToExpire(5);
        verify(taskNotificationGateway).findTasksAboutToExpire(1);
        verify(taskNotificationGateway, never()).sendNotification(any());
    }

    @Test
    void givenTasksAboutToExpire_whenNotifyTasksAboutToExpire_thenNotificationsSentForEach() throws SystemException {
        // Given
        Task t60 = new Task();
        Task t5 = new Task();
        when(taskNotificationGateway.findTasksAboutToExpire(60)).thenReturn(List.of(t60));
        when(taskNotificationGateway.findTasksAboutToExpire(30)).thenReturn(List.of());
        when(taskNotificationGateway.findTasksAboutToExpire(5)).thenReturn(List.of(t5));
        when(taskNotificationGateway.findTasksAboutToExpire(1)).thenReturn(List.of());

        // When
        useCase.notifyTasksAboutToExpire();

        // Then
        verify(taskNotificationGateway).findTasksAboutToExpire(60);
        verify(taskNotificationGateway).findTasksAboutToExpire(30);
        verify(taskNotificationGateway).findTasksAboutToExpire(5);
        verify(taskNotificationGateway).findTasksAboutToExpire(1);
        verify(taskNotificationGateway, times(2)).sendNotification(taskCaptor.capture());
        List<Task> sent = taskCaptor.getAllValues();
        assertThat(sent).containsExactlyInAnyOrderElementsOf(List.of(t60, t5));
    }
}
