package br.com.gerenciador.domain.model;

import br.com.gerenciador.domain.enums.TaskStatusEnum;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private UUID userId;
    private UUID id;
    private String title;
    private String description;
    private TaskStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime dueAt;

    public Task(UUID userId, UUID id, String title, String description, TaskStatusEnum status, LocalDateTime createdAt, LocalDateTime dueAt) throws TaskException {
        this.userId = userId;
        this.id = id;
        setTitle(title);
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        setDueAt(dueAt);
    }

    public Task() {
    }

    public void setTitle(String title) throws TaskException {
        if (title == null || title.trim().isEmpty()) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0006.getMessage(),
                    ErrorCodeEnum.TASK0006.getCode()
            );
        }

        if (title.trim().length() < 20) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0007.getMessage(),
                    ErrorCodeEnum.TASK0007.getCode()
            );
        }
        if (title.trim().length() > 255) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0009.getMessage(),
                    ErrorCodeEnum.TASK0009.getCode()
            );
        }

        this.title = title;
    }

    public void setDueAt(LocalDateTime dueAt) throws TaskException {
        if (dueAt == null) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0004.getMessage(),
                    ErrorCodeEnum.TASK0004.getCode()
            );
        }
        if (this.id == null && dueAt.isBefore(LocalDateTime.now())) {
            throw new TaskException(
                    ErrorCodeEnum.TASK0004.getMessage(),
                    ErrorCodeEnum.TASK0004.getCode()
            );
        }

        this.dueAt = dueAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TaskStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(userId, task.userId) && Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status && Objects.equals(createdAt, task.createdAt) && Objects.equals(updatedAt, task.updatedAt) && Objects.equals(deletedAt, task.deletedAt) && Objects.equals(dueAt, task.dueAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, id, title, description, status, createdAt, updatedAt, deletedAt, dueAt);
    }
}
