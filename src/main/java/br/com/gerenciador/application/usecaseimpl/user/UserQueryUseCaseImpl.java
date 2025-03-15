package br.com.gerenciador.application.usecaseimpl.user;
import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.User;
import br.com.gerenciador.usecase.user.UserQueryUseCase;
import java.util.Optional;
import java.util.UUID;

public class UserQueryUseCaseImpl implements UserQueryUseCase {

    private final UserQueryGateway userQueryGateway;

    public UserQueryUseCaseImpl(UserQueryGateway userQueryGateway) {
        this.userQueryGateway = userQueryGateway;
    }

    @Override
    public Optional<User> findById(UUID userId) throws UserException {
        return userQueryGateway.findById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) throws UserException {
        return userQueryGateway.findByEmail(email);
    }
}