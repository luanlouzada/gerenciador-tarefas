package br.com.gerenciador.infrastructure.mapper;

import br.com.gerenciador.domain.model.User;
import br.com.gerenciador.infrastructure.dto.request.user.RegistrationUserRequest;
import br.com.gerenciador.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toUserEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.getEmail()
        );
    }

    public User toUser(RegistrationUserRequest request) throws Exception {
        return new User(
                request.name(),
                request.password(),
                request.email()
        );
    }

    public User toUser(UserEntity userEntity) throws Exception {
        return new User(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getPassword(),
                userEntity.getEmail()
        );
    }
}