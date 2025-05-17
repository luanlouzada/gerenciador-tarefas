package br.com.gerenciador.infrastructure.controller.task;

import br.com.gerenciador.domain.enums.TaskStatusEnum;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.dto.request.task.TaskCreationRequest;
import br.com.gerenciador.infrastructure.dto.request.task.TaskUpdateRequest;
import br.com.gerenciador.infrastructure.entity.UserEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.UserEntityRepository;
import br.com.gerenciador.usecase.task.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerUnitTest {

    @Mock
    private TaskCreationUseCase taskCreationUseCase;
    @Mock
    private TaskListingUseCase taskListingUseCase;
    @Mock
    private TaskQueryUseCase taskQueryUseCase;
    @Mock
    private TaskUpdateUseCase taskUpdateUseCase;
    @Mock
    private TaskDeletionUseCase taskDeletionUseCase;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private UserEntityRepository userEntityRepository;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskController controller;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    @Test
    void givenValidCreationRequest_whenCreateTask_thenReturnsCreatedResponse() throws TaskException, UserException {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "Test task title 12345";
        String description = "Some description";
        String dueAt = LocalDateTime.now().plusDays(1).toString();
        TaskCreationRequest request = new TaskCreationRequest(title, description, dueAt);
        UserEntity userEntity = new UserEntity();
        Task taskToCreate = new Task();
        Task createdTask = new Task();
        when(authentication.getPrincipal()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(taskMapper.toTask(request, userEntity)).thenReturn(taskToCreate);
        when(taskCreationUseCase.createTask(taskToCreate)).thenReturn(createdTask);

        // When
        ResponseEntity<?> response = controller.createTask(authentication, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var body = (br.com.gerenciador.infrastructure.dto.response.BaseResponse<Task>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getSuccess()).isTrue();
        assertThat(body.getResult()).isEqualTo(createdTask);
        verify(taskCreationUseCase).createTask(taskToCreate);
    }

    @Test
    void givenUserNotFound_whenCreateTask_thenThrowsUserException() {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "Title 1234567890";
        String description = "Desc";
        String dueAt = LocalDateTime.now().toString();
        TaskCreationRequest request = new TaskCreationRequest(title, description, dueAt);
        when(authentication.getPrincipal()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserException.class, () -> controller.createTask(authentication, request));
    }

    @Test
    void givenTasksExist_whenGetAllTasks_thenReturnsSortedTasks() throws TaskException, UserException {
        // Given
        UUID userId = UUID.randomUUID();
        Task t1 = new Task();
        t1.setDueAt(LocalDateTime.now().plusDays(1));
        Task t2 = new Task();
        t2.setDueAt(LocalDateTime.now().plusHours(1));
        List<Task> unsorted = Arrays.asList(t1, t2);
        when(authentication.getPrincipal()).thenReturn(userId);
        when(taskListingUseCase.listByUserId(userId)).thenReturn(unsorted);

        // When
        ResponseEntity<?> response = controller.getAllTasks(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (br.com.gerenciador.infrastructure.dto.response.BaseResponse<List<Task>>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getResult()).containsExactly(t2, t1);
        verify(taskListingUseCase).listByUserId(userId);
    }

    @Test
    void givenTaskExists_whenGetTaskById_thenReturnsTask() throws TaskException {
        // Given
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        when(authentication.getPrincipal()).thenReturn(UUID.randomUUID());
        when(taskQueryUseCase.findById(taskId)).thenReturn(Optional.of(task));

        // When
        ResponseEntity<?> response = controller.getTaskById(authentication, taskId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (br.com.gerenciador.infrastructure.dto.response.BaseResponse<Task>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getResult()).isEqualTo(task);
        verify(taskQueryUseCase).findById(taskId);
    }

    @Test
    void givenTaskNotFound_whenGetTaskById_thenThrowsTaskException() throws TaskException {
        // Given
        UUID taskId = UUID.randomUUID();
        when(authentication.getPrincipal()).thenReturn(UUID.randomUUID());
        when(taskQueryUseCase.findById(taskId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(TaskException.class, () -> controller.getTaskById(authentication, taskId));
    }

    @Test
    void givenValidUpdateRequest_whenUpdateTask_thenReturnsUpdatedTask() throws TaskException, UserException {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String title = "Updated Title 12345aaaaaaaaaaaaaaaaaaa";
        String description = "Updated Desc";
        String dueAt = LocalDateTime.now().plusDays(2).toString();
        TaskUpdateRequest request = new TaskUpdateRequest(title, description, dueAt, TaskStatusEnum.PENDING);
        UserEntity userEntity = new UserEntity();
        Task mappedTask = new Task();
        mappedTask.setTitle(title);
        Task updatedTask = new Task();
        updatedTask.setTitle(title);
        when(authentication.getPrincipal()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(taskMapper.toTask(request, userEntity)).thenReturn(mappedTask);
        when(taskUpdateUseCase.update(eq(taskId), eq(mappedTask))).thenReturn(updatedTask);

        // When
        ResponseEntity<?> response = controller.updateTask(authentication, taskId, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (br.com.gerenciador.infrastructure.dto.response.BaseResponse<Task>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getResult()).isEqualTo(updatedTask);
        assertThat(body.getSuccess()).isTrue();
        verify(taskUpdateUseCase).update(eq(taskId), eq(mappedTask));
    }

    @Test
    void givenExistingTask_whenDeleteTask_thenReturnsOkResponse() throws TaskException {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(authentication.getPrincipal()).thenReturn(userId);
        doNothing().when(taskDeletionUseCase).delete(userId, taskId);

        // When
        ResponseEntity<?> response = controller.deleteTask(authentication, taskId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (br.com.gerenciador.infrastructure.dto.response.BaseResponse<Void>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getSuccess()).isTrue();
        verify(taskDeletionUseCase, times(1)).delete(userId, taskId);
    }
}
