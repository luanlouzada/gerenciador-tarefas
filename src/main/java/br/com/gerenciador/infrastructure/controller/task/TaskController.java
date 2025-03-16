package br.com.gerenciador.infrastructure.controller.task;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.dto.request.task.TaskCreationRequest;
import br.com.gerenciador.infrastructure.dto.response.BaseResponse;
import br.com.gerenciador.infrastructure.entity.UserEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.UserEntityRepository;
import br.com.gerenciador.usecase.task.TaskCreationUseCase;
import br.com.gerenciador.usecase.task.TaskDeletionUseCase;
import br.com.gerenciador.usecase.task.TaskListingUseCase;
import br.com.gerenciador.usecase.task.TaskQueryUseCase;
import br.com.gerenciador.usecase.task.TaskUpdateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/task")
@Tag(name = "Task", description = "Endpoints for task management")
@SecurityRequirement(name = "bearerAuth")
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

        @Operation(summary = "Create a new task", description = "Creates a new task for the authenticated user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "409", description = "Task with this title already exists")
        })
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

        @Operation(summary = "List all tasks", description = "Lists all tasks for the authenticated user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Tasks listed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
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

        @Operation(summary = "Get task by ID", description = "Retrieves a single task by its ID for the authenticated user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Task found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Task not found")
        })
        @GetMapping("/{taskId}")
        public ResponseEntity<BaseResponse<Task>> getTaskById(
                        Authentication authentication,
                        @Parameter(description = "ID of the task to retrieve", required = true) @PathVariable UUID taskId)
                        throws TaskException {
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

        @Operation(summary = "Update task", description = "Updates an existing task for the authenticated user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Task not found")
        })
        @PutMapping("/{taskId}")
        public ResponseEntity<BaseResponse<Task>> updateTask(
                        Authentication authentication,
                        @Parameter(description = "ID of the task to update", required = true) @PathVariable UUID taskId,
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

        @Operation(summary = "Delete task", description = "Deletes an existing task for the authenticated user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Task deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Task not found")
        })
        @DeleteMapping("/{taskId}")
        public ResponseEntity<BaseResponse<Void>> deleteTask(
                        Authentication authentication,
                        @Parameter(description = "ID of the task to delete", required = true) @PathVariable UUID taskId)
                        throws TaskException {
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