package br.com.gerenciador.infrastructure.config;

import br.com.gerenciador.application.gateway.task.*;
import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.application.usecaseimpl.task.*;
import br.com.gerenciador.usecase.task.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskConfig {
    @Bean
    public TaskCreationUseCase taskCreationUseCase(TaskCreationGateway taskCreationGateway,
                                                   UserQueryGateway userQueryGateway, TaskQueryGateway taskQueryGateway) {
        return new TaskCreationUseCaseImpl(taskCreationGateway, userQueryGateway, taskQueryGateway);
    }

    @Bean
    public TaskDeletionUseCase taskDeletionUseCase(TaskDeletionGateway taskDeletionGateway,
                                                   TaskQueryGateway taskQueryGateway) {
        return new TaskDeletionUseCaseImpl(taskDeletionGateway, taskQueryGateway);
    }

    @Bean
    public TaskListingUseCase taskListingUseCase(TaskListingGateway taskListingGateway,
                                                 UserQueryGateway userQueryGateway) {
        return new TaskListingUseCaseImpl(taskListingGateway, userQueryGateway);
    }

    @Bean
    public TaskNotificationUseCase taskNotificationUseCase(TaskNotificationGateway taskNotificationGateway) {
        return new TaskNotificationUseCaseImpl(taskNotificationGateway);
    }

    @Bean
    public TaskQueryUseCase taskQueryUseCase(TaskQueryGateway taskQueryGateway) {
        return new TaskQueryUseCaseImpl(taskQueryGateway);
    }

    @Bean
    public TaskUpdateUseCase taskUpdateUseCase(TaskUpdateGateway taskUpdateGateway,
                                               TaskQueryGateway taskQueryGateway) {
        return new TaskUpdateUseCaseImpl(taskUpdateGateway, taskQueryGateway);
    }

}
