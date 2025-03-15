package br.com.gerenciador.application.gateway.user;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserQueryGateway {
    Optional<User> findById(UUID userId) throws UserException;
    Optional<User> findByEmail(String email) throws UserException;
}