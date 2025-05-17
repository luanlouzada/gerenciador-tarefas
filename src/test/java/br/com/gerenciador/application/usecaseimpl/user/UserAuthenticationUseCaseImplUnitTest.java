package br.com.gerenciador.application.usecaseimpl.user;

import br.com.gerenciador.application.gateway.user.UserAuthenticationGateway;
import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationUseCaseImplUnitTest {

    @Mock
    private UserAuthenticationGateway userAuthenticationGateway;

    @Mock
    private UserQueryGateway userQueryGateway;

    @InjectMocks
    private UserAuthenticationUseCaseImpl useCase;

    @Test
    void givenUserNotFound_whenAuthenticate_thenThrowsUserException() throws UserException {
        // Given: no user for provided email
        String email = "notfound@example.com";
        String password = "pass";
        when(userQueryGateway.findByEmail(email)).thenReturn(Optional.empty());

        // When + Then: expect USER0001
        UserException ex = assertThrows(UserException.class, () -> useCase.authenticate(email, password));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.USER0001.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.USER0001.getMessage());

        verify(userQueryGateway).findByEmail(email);
        verifyNoInteractions(userAuthenticationGateway);
    }

    @Test
    void givenInvalidCredentials_whenAuthenticate_thenThrowsUserException() throws UserException {
        // Given: user exists but authentication fails
        String email = "user@example.com";
        String password = "wrongpass";
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        when(userQueryGateway.findByEmail(email)).thenReturn(Optional.of(user));
        when(userAuthenticationGateway.authenticate(email, password)).thenReturn(false);

        // When + Then: expect AUTH0001
        UserException ex = assertThrows(UserException.class, () -> useCase.authenticate(email, password));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.AUTH0001.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.AUTH0001.getMessage());

        verify(userQueryGateway).findByEmail(email);
        verify(userAuthenticationGateway).authenticate(email, password);
    }

    @Test
    void givenValidCredentials_whenAuthenticate_thenReturnsUserId() throws UserException {
        // Given: user exists and authentication succeeds
        String email = "valid@example.com";
        String password = "correctpass";
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        when(userQueryGateway.findByEmail(email)).thenReturn(Optional.of(user));
        when(userAuthenticationGateway.authenticate(email, password)).thenReturn(true);

        // When
        UUID result = useCase.authenticate(email, password);

        // Then: verify and assert returned ID
        verify(userQueryGateway).findByEmail(email);
        verify(userAuthenticationGateway).authenticate(email, password);
        assertThat(result).isEqualTo(userId);
    }
}
