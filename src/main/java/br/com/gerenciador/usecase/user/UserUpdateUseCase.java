package br.com.gerenciador.usecase.user;

import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.User;

import java.util.UUID;

public interface UserUpdateUseCase {
    User update(UUID userId, User userUpdateData) throws UserException;
}