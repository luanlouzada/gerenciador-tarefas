package br.com.gerenciador.infrastructure.controller.task;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.dto.request.task.TaskCreationRequest;
import br.com.gerenciador.infrastructure.dto.response.BaseResponse;
import br.com.gerenciador.infrastructure.entity.UserEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.UserEntityRepository;
import br.com.gerenciador.usecase.task.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@RestController
@RequestMapping("api/v1/task")
public class TaskController {
    private final TaskCreationUseCase taskCreationUseCase;
    private final TaskDeletionUseCase taskDeletionUseCase;
    private final TaskListingUseCase taskListingUseCase;
    private final TaskQueryUseCase taskQueryUseCase;
    private final TaskUpdateUseCase taskUpdateUseCase;
    private final TaskMapper taskMapper;
    private final UserEntityRepository userEntityRepository;

    public TaskController(TaskCreationUseCase taskCreationUseCase,
                          TaskDeletionUseCase taskDeletionUseCase,
                          TaskListingUseCase taskListingUseCase,
                          TaskQueryUseCase taskQueryUseCase,
                          TaskUpdateUseCase taskUpdateUseCase,
                          TaskMapper taskMapper,
                          UserEntityRepository userEntityRepository) {
        this.taskCreationUseCase = taskCreationUseCase;
        this.taskDeletionUseCase = taskDeletionUseCase;
        this.taskListingUseCase = taskListingUseCase;
        this.taskQueryUseCase = taskQueryUseCase;
        this.taskUpdateUseCase = taskUpdateUseCase;
        this.taskMapper = taskMapper;
        this.userEntityRepository = userEntityRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse<Task>> createTask(
            Authentication authentication,
            @Valid @RequestBody TaskCreationRequest request) throws TaskException, UserException {
        log.info("Início da criação de tarefa::TaskController");

        UUID userId = (UUID) authentication.getPrincipal();

        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCodeEnum.USER0001.getMessage(),
                        ErrorCodeEnum.USER0001.getCode()));

        Task task = taskMapper.toTask(request, userEntity);
        Task createdTask = taskCreationUseCase.createTask(task);

        log.info("Tarefa criada com sucesso::TaskController");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<Task>builder()
                        .result(createdTask)
                        .success(true)
                        .message("Tarefa criada com sucesso")
                        .build());
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Task>>> getAllTasks(
            Authentication authentication) throws TaskException, UserException {
        log.info("Início da listagem de tarefas::TaskController");

        UUID userId = (UUID) authentication.getPrincipal();
        List<Task> tasks = taskListingUseCase.listByUserId(userId);
        tasks.sort(Comparator.comparing(Task::getDueAt));

        log.info("Tarefas listadas com sucesso::TaskController");
        return ResponseEntity.ok(BaseResponse.<List<Task>>builder()
                .result(tasks)
                .success(true)
                .message("Tarefas listadas com sucesso")
                .build());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<BaseResponse<Task>> getTaskById(
            Authentication authentication,
            @PathVariable UUID taskId) throws TaskException {
        log.info("Início da busca de tarefa por ID::TaskController");

        UUID userId = (UUID) authentication.getPrincipal();
        Task task = taskQueryUseCase.findById(taskId)
                .orElseThrow(() -> new TaskException(ErrorCodeEnum.TASK0001.getMessage(),
                        ErrorCodeEnum.TASK0001.getCode()));

        log.info("Tarefa encontrada com sucesso::TaskController");
        return ResponseEntity.ok(BaseResponse.<Task>builder()
                .result(task)
                .success(true)
                .message("Tarefa encontrada com sucesso")
                .build());
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<BaseResponse<Task>> updateTask(
            Authentication authentication,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskCreationRequest request) throws TaskException, UserException {
        log.info("Início da atualização de tarefa::TaskController");

        UUID userId = (UUID) authentication.getPrincipal();
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCodeEnum.USER0001.getMessage(),
                        ErrorCodeEnum.USER0001.getCode()));

        Task task = taskMapper.toTask(request, userEntity);
        task.setId(taskId);
        Task updatedTask = taskUpdateUseCase.update(taskId, task);

        log.info("Tarefa atualizada com sucesso::TaskController");
        return ResponseEntity.ok(BaseResponse.<Task>builder()
                .result(updatedTask)
                .success(true)
                .message("Tarefa atualizada com sucesso")
                .build());
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<BaseResponse<Void>> deleteTask(
            Authentication authentication,
            @PathVariable UUID taskId) throws TaskException {
        log.info("Início da exclusão de tarefa::TaskController");

        UUID userId = (UUID) authentication.getPrincipal();
        taskDeletionUseCase.delete(userId, taskId);

        log.info("Tarefa excluída com sucesso::TaskController");
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .success(true)
                .message("Tarefa excluída com sucesso")
                .build());
    }
}