package br.com.gerenciador.application.gateway.user;

import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.User;

public interface UserRegistrationGateway {
    Boolean registerUser(User user) throws UserException;
}
