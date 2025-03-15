package br.com.gerenciador.application.usecaseimpl.user;

import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.application.gateway.user.UserRegistrationGateway;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.User;
import br.com.gerenciador.usecase.user.UserRegistrationUseCase;

public class UserRegistrationUseCaseImpl implements UserRegistrationUseCase {

    private final UserRegistrationGateway userRegistrationGateway;
    private final UserQueryGateway userQueryGateway;

    public UserRegistrationUseCaseImpl(
            UserRegistrationGateway userRegistrationGateway,
            UserQueryGateway userQueryGateway) {
        this.userRegistrationGateway = userRegistrationGateway;
        this.userQueryGateway = userQueryGateway;
    }

    @Override
    public void registerUser(User user) throws UserException {
        try {
            var existingUser = userQueryGateway.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                throw new UserException(
                        ErrorCodeEnum.USER0002.getMessage(),
                        ErrorCodeEnum.USER0002.getCode()
                );
            }

            Boolean registered = userRegistrationGateway.registerUser(user);
            if (!registered) {
                throw new UserException(
                        ErrorCodeEnum.USER0004.getMessage(),
                        ErrorCodeEnum.USER0004.getCode()
                );
            }
        } catch (UserException e) {

            if (ErrorCodeEnum.USER0002.getCode().equals(e.getCode())) {
                throw e;
            }

            throw new UserException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode()
            );
        }
    }
}