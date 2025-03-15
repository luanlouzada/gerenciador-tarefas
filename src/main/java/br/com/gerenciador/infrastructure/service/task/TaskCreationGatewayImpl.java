package br.com.gerenciador.infrastructure.service.task;

import br.com.gerenciador.application.gateway.task.TaskCreationGateway;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.infrastructure.entity.UserEntity;
import br.com.gerenciador.infrastructure.mapper.TaskMapper;
import br.com.gerenciador.infrastructure.repository.TaskEntityRepository;
import br.com.gerenciador.infrastructure.repository.UserEntityRepository;
import org.springframework.stereotype.Service;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class TaskCreationGatewayImpl implements TaskCreationGateway {

    private final TaskMapper taskMapper;
    private final TaskEntityRepository taskEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public TaskCreationGatewayImpl(
            TaskEntityRepository taskEntityRepository,
            UserEntityRepository userEntityRepository,
            TaskMapper taskMapper) {
        this.taskEntityRepository = taskEntityRepository;
        this.userEntityRepository = userEntityRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public Task createTask(Task task) throws TaskException {
        try {
            UserEntity userEntity = userEntityRepository.findById(task.getUserId())
                    .orElseThrow(() -> new TaskException("Usuário não encontrado", "USER0001"));

            var taskEntity = taskMapper.toTaskEntity(task, userEntity);
            var savedTaskEntity = taskEntityRepository.save(taskEntity);
            return taskMapper.toTask(savedTaskEntity);
        } catch (TaskException e) {
            log.error("Erro ao criar tarefa::TaskCreationGatewayImpl", e);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao criar tarefa::TaskCreationGatewayImpl", e);
            throw new TaskException("Erro ao criar tarefa", "TASK0003");
        }
    }
}