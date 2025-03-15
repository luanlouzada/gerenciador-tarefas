package br.com.gerenciador.usecase.user;

import br.com.gerenciador.domain.exception.UserException;
import java.util.UUID;

public interface UserAuthenticationUseCase {
    UUID authenticate(String email, String password) throws UserException;
}