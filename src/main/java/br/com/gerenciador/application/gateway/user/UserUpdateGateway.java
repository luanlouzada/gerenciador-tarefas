package br.com.gerenciador.application.gateway.user;

import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.User;

import java.util.UUID;

public interface UserUpdateGateway {
    Boolean updateUser(UUID userId, User user) throws UserException;
}