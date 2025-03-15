package br.com.gerenciador.application.usecaseimpl.user;

import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.application.gateway.user.UserUpdateGateway;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.User;
import br.com.gerenciador.usecase.user.UserUpdateUseCase;

import java.util.UUID;

public class UserUpdateUseCaseImpl implements UserUpdateUseCase {

    private final UserUpdateGateway userUpdateGateway;
    private final UserQueryGateway userQueryGateway;

    public UserUpdateUseCaseImpl(
            UserUpdateGateway userUpdateGateway,
            UserQueryGateway userQueryGateway) {
        this.userUpdateGateway = userUpdateGateway;
        this.userQueryGateway = userQueryGateway;
    }

    @Override
    public User update(UUID userId, User userUpdateData) throws UserException {

        User existingUser = userQueryGateway.findById(userId)
                .orElseThrow(() -> new UserException(
                        ErrorCodeEnum.USER0001.getMessage(),
                        ErrorCodeEnum.USER0001.getCode()
                ));


        if (userUpdateData.getEmail() != null && !userUpdateData.getEmail().equals(existingUser.getEmail())) {
            var existingEmail = userQueryGateway.findByEmail(userUpdateData.getEmail());
            if (existingEmail.isPresent() && !existingEmail.get().getId().equals(userId)) {
                throw new UserException(
                        ErrorCodeEnum.USER0002.getMessage(),
                        ErrorCodeEnum.USER0002.getCode()
                );
            }
        }


        if (userUpdateData.getName() != null) {
            existingUser.setName(userUpdateData.getName());
        }
        if (userUpdateData.getEmail() != null) {
            existingUser.setEmail(userUpdateData.getEmail());
        }
        if (userUpdateData.getPassword() != null) {
            existingUser.setPassword(userUpdateData.getPassword());
        }


        Boolean updated = userUpdateGateway.updateUser(userId, existingUser);
        if (!updated) {
            throw new UserException(
                    ErrorCodeEnum.SYS0001.getMessage(),
                    ErrorCodeEnum.SYS0001.getCode()
            );
        }

        return existingUser;
    }
}