package br.com.gerenciador.infrastructure.service.user;

import br.com.gerenciador.application.gateway.user.UserRegistrationGateway;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.User;
import br.com.gerenciador.infrastructure.mapper.UserMapper;
import br.com.gerenciador.infrastructure.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class UserRegistrationGatewayImpl implements UserRegistrationGateway {

    private final UserMapper userMapper;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationGatewayImpl(UserEntityRepository userEntityRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Boolean registerUser(User user) throws UserException {
        try {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            var userSaved = userEntityRepository.save(userMapper.toUserEntity(user));
            return true;
        } catch (Exception e) {
            log.error("Error ao registrar o usuário::registerUserGatewayImpl", e);
            return false;
        }
    }
}
