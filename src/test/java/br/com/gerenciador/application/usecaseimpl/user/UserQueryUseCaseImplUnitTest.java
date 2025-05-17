package br.com.gerenciador.application.usecaseimpl.user;

import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.UserException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserQueryUseCaseImplUnitTest {

    @Mock
    private UserQueryGateway userQueryGateway;

    @InjectMocks
    private UserQueryUseCaseImpl useCase;

    @Test
    void givenGatewayThrowsException_whenFindById_thenThrowsUserException() throws UserException {
        // Given
        UUID userId = UUID.randomUUID();
        when(userQueryGateway.findById(userId)).thenThrow(new UserException("fail findById", "USR001"));

        // When + Then
        assertThrows(UserException.class, () -> useCase.findById(userId));
        verify(userQueryGateway).findById(userId);
    }

    @Test
    void givenUserNotFound_whenFindById_thenReturnsEmptyOptional() throws UserException {
        // Given
        UUID userId = UUID.randomUUID();
        when(userQueryGateway.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = useCase.findById(userId);

        // Then
        assertThat(result).isEmpty();
        verify(userQueryGateway).findById(userId);
    }

    @Test
    void givenUserFound_whenFindById_thenReturnsUser() throws UserException {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = useCase.findById(userId);

        // Then
        assertThat(result).isPresent().containsSame(user);
        verify(userQueryGateway).findById(userId);
    }

    @Test
    void givenGatewayThrowsException_whenFindByEmail_thenThrowsUserException() throws UserException {
        // Given
        String email = "fail@example.com";
        when(userQueryGateway.findByEmail(email)).thenThrow(new UserException("fail findByEmail", "USR002"));

        // When + Then
        assertThrows(UserException.class, () -> useCase.findByEmail(email));
        verify(userQueryGateway).findByEmail(email);
    }

    @Test
    void givenUserNotFound_whenFindByEmail_thenReturnsEmptyOptional() throws UserException {
        // Given
        String email = "notfound@example.com";
        when(userQueryGateway.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = useCase.findByEmail(email);

        // Then
        assertThat(result).isEmpty();
        verify(userQueryGateway).findByEmail(email);
    }

    @Test
    void givenUserFound_whenFindByEmail_thenReturnsUser() throws UserException {
        // Given
        String email = "found@example.com";
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        when(userQueryGateway.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = useCase.findByEmail(email);

        // Then
        assertThat(result).isPresent().containsSame(user);
        verify(userQueryGateway).findByEmail(email);
    }
}
