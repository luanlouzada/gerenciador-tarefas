package br.com.gerenciador.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gerenciador.domain.enums.TaskStatusEnum;
import br.com.gerenciador.infrastructure.entity.TaskEntity;
import br.com.gerenciador.infrastructure.entity.UserEntity;

@Repository
public interface TaskEntityRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByUser(UserEntity user);

    List<TaskEntity> findByUserAndTitleContainingIgnoreCase(UserEntity user, String title);

    List<TaskEntity> findByUserIdAndDueAtBetween(UUID userId, LocalDateTime start, LocalDateTime end);

    List<TaskEntity> findByDueAtBetweenAndDeletedAtIsNull(LocalDateTime start, LocalDateTime end);

    List<TaskEntity> findByUserIdAndStatusNot(UUID userId, TaskStatusEnum status);

    List<TaskEntity> findByUserIdAndDeletedAtIsNull(UUID userId);

    List<TaskEntity> findByUserIdAndTitleContainingAndDeletedAtIsNull(UUID userId, String title);

    List<TaskEntity> findByUserIdAndDueAtAndDeletedAtIsNull(UUID userId, LocalDateTime dueAt);

    boolean existsByUserIdAndTitleIgnoreCaseAndDeletedAtIsNull(UUID userId, String title);

    List<TaskEntity> findByDueAtBetweenAndDeletedAtIsNullAndStatusNot(
            LocalDateTime start,
            LocalDateTime end,
            TaskStatusEnum status);
}
