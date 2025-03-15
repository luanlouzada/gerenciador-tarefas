package br.com.gerenciador.infrastructure.service.user;

import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.User;
import br.com.gerenciador.infrastructure.entity.UserEntity;
import br.com.gerenciador.infrastructure.mapper.UserMapper;
import br.com.gerenciador.infrastructure.repository.UserEntityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class UserQueryGatewayImpl implements UserQueryGateway {

    private final UserEntityRepository userEntityRepository;
    private final UserMapper userMapper;

    public UserQueryGatewayImpl(
            UserEntityRepository userEntityRepository,
            UserMapper userMapper) {
        this.userEntityRepository = userEntityRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findById(UUID userId) throws UserException {
        try {
            Optional<UserEntity> userEntityOptional = userEntityRepository.findById(userId);

            if (userEntityOptional.isEmpty()) {
                return Optional.empty();
            }

            User user = userMapper.toUser(userEntityOptional.get());
            return Optional.of(user);
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por ID::UserQueryGatewayImpl", e);
            throw new UserException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode()
            );
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws UserException {
        try {
            Optional<UserEntity> userEntityOptional = userEntityRepository.findByEmail(email);

            if (userEntityOptional.isEmpty()) {
                return Optional.empty();
            }

            User user = userMapper.toUser(userEntityOptional.get());
            return Optional.of(user);
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por email::UserQueryGatewayImpl", e);
            throw new UserException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode()
            );
        }
    }
}