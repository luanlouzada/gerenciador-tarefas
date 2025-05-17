package br.com.gerenciador.application.usecaseimpl.user;

import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.application.gateway.user.UserRegistrationGateway;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationUseCaseImplUnitTest {

    @Mock
    private UserRegistrationGateway userRegistrationGateway;

    @Mock
    private UserQueryGateway userQueryGateway;

    @InjectMocks
    private UserRegistrationUseCaseImpl useCase;

    @Test
    void givenExistingEmail_whenRegisterUser_thenThrowsUserExceptionUSER0002() throws UserException {
        // Given: user with email already exists
        User input = new User();
        input.setEmail("already@exists.com");
        when(userQueryGateway.findByEmail(input.getEmail()))
                .thenReturn(java.util.Optional.of(input));

        // When/Then: expect USER0002
        assertThatThrownBy(() -> useCase.registerUser(input))
                .isInstanceOf(UserException.class)
                .matches(e -> ((UserException) e).getCode().equals(ErrorCodeEnum.USER0002.getCode()),
                        "Expected code USER0002")
                .matches(e -> ((UserException) e).getMessage().equals(ErrorCodeEnum.USER0002.getMessage()),
                        "Expected message USER0002");

        verify(userQueryGateway).findByEmail(input.getEmail());
        verifyNoInteractions(userRegistrationGateway);
    }

    @Test
    void givenRegistrationFails_whenRegisterUser_thenThrowsUserExceptionSYS0001() throws UserException {
        // Given: no existing user, but registration returns false
        User input = new User();
        input.setEmail("new@user.com");
        when(userQueryGateway.findByEmail(input.getEmail()))
                .thenReturn(java.util.Optional.empty());
        when(userRegistrationGateway.registerUser(input)).thenReturn(false);

        // When/Then: expect SYS0001 (wrap of USER0004)
        assertThatThrownBy(() -> useCase.registerUser(input))
                .isInstanceOf(UserException.class)
                .matches(e -> ((UserException) e).getCode().equals(ErrorCodeEnum.SYS0001.getCode()),
                        "Expected code SYS0001")
                .matches(e -> ((UserException) e).getMessage().equals(ErrorCodeEnum.SYS0001.getMessage()),
                        "Expected message SYS0001");

        verify(userQueryGateway).findByEmail(input.getEmail());
        verify(userRegistrationGateway).registerUser(input);
    }

    @Test
    void givenValidRegistration_whenRegisterUser_thenDoesNotThrow() throws UserException {
        // Given: no existing user, registration succeeds
        User input = new User();
        input.setEmail("valid@new.com");
        when(userQueryGateway.findByEmail(input.getEmail()))
                .thenReturn(java.util.Optional.empty());
        when(userRegistrationGateway.registerUser(input)).thenReturn(true);

        // When/Then: no exception
        assertDoesNotThrow(() -> useCase.registerUser(input));

        verify(userQueryGateway).findByEmail(input.getEmail());
        verify(userRegistrationGateway).registerUser(input);
    }
}
