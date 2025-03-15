package br.com.gerenciador.usecase.user;

import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.User;

public interface UserRegistrationUseCase {
    void registerUser(User user) throws UserException;
}