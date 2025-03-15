package br.com.gerenciador.infrastructure.service.user;

import br.com.gerenciador.application.gateway.user.UserAuthenticationGateway;
import br.com.gerenciador.domain.model.User;
import br.com.gerenciador.infrastructure.entity.UserEntity;
import br.com.gerenciador.infrastructure.mapper.UserMapper;
import br.com.gerenciador.infrastructure.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

@Service
public class UserAuthenticationGatewayImpl implements UserAuthenticationGateway {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserAuthenticationGatewayImpl(
            UserEntityRepository userEntityRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public Boolean authenticate(String email, String password) {
        try {
            Optional<UserEntity> userOptional = userEntityRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                return false;
            }

            UserEntity userEntity = userOptional.get();
            User user = userMapper.toUser(userEntity);

            return passwordEncoder.matches(password, user.getPassword());
        } catch (Exception e) {
            log.error("Erro ao autenticar usuário::UserAuthenticationGatewayImpl", e);
            return false;
        }
    }
}