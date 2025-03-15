package br.com.gerenciador.application.usecaseimpl.user;

import br.com.gerenciador.application.gateway.user.UserAuthenticationGateway;
import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.usecase.user.UserAuthenticationUseCase;

import java.util.UUID;

public class UserAuthenticationUseCaseImpl implements UserAuthenticationUseCase {

    private final UserAuthenticationGateway userAuthenticationGateway;
    private final UserQueryGateway userQueryGateway;

    public UserAuthenticationUseCaseImpl(
            UserAuthenticationGateway userAuthenticationGateway,
            UserQueryGateway userQueryGateway) {
        this.userAuthenticationGateway = userAuthenticationGateway;
        this.userQueryGateway = userQueryGateway;
    }

    @Override
    public UUID authenticate(String email, String password) throws UserException {
        var user = userQueryGateway.findByEmail(email)
                .orElseThrow(() -> new UserException(
                        ErrorCodeEnum.USER0001.getMessage(),
                        ErrorCodeEnum.USER0001.getCode()
                ));


        Boolean isAuthenticated = userAuthenticationGateway.authenticate(email, password);
        if (!isAuthenticated) {
            throw new UserException(
                    ErrorCodeEnum.AUTH0001.getMessage(),
                    ErrorCodeEnum.AUTH0001.getCode()
            );
        }

        return user.getId();
    }
}