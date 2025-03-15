package br.com.gerenciador.infrastructure.service.user;

import br.com.gerenciador.application.gateway.user.UserUpdateGateway;
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
public class UserUpdateGatewayImpl implements UserUpdateGateway {

    private final UserEntityRepository userEntityRepository;
    private final UserMapper userMapper;

    public UserUpdateGatewayImpl(
            UserEntityRepository userEntityRepository,
            UserMapper userMapper) {
        this.userEntityRepository = userEntityRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Boolean updateUser(UUID userId, User user) throws UserException {
        try {
            Optional<UserEntity> userOptional = userEntityRepository.findById(userId);

            if (userOptional.isEmpty()) {
                throw new UserException(
                        ErrorCodeEnum.USER0001.getMessage(),
                        ErrorCodeEnum.USER0001.getCode());
            }

            UserEntity userEntity = userOptional.get();
            userEntity.setName(user.getName());
            userEntity.setEmail(user.getEmail());

            userEntityRepository.save(userEntity);
            return true;
        } catch (UserException e) {
            log.error("Erro ao atualizar usuário::UserUpdateGatewayImpl", e);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário::UserUpdateGatewayImpl", e);
            throw new UserException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode());
        }
    }
}