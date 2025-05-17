package br.com.gerenciador.application.usecaseimpl.user;

import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.application.gateway.user.UserUpdateGateway;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUpdateUseCaseImplUnitTest {

    @Mock
    private UserUpdateGateway userUpdateGateway;

    @Mock
    private UserQueryGateway userQueryGateway;

    @InjectMocks
    private UserUpdateUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        reset(userUpdateGateway, userQueryGateway);
    }

    @Test
    void givenUserNotFound_whenUpdate_thenThrowsUserExceptionUSER0001() throws UserException {
        // Given: no user exists with given ID
        when(userQueryGateway.findById(userId)).thenReturn(Optional.empty());
        User updateData = new User();

        // When + Then: expect USER0001
        UserException ex = assertThrows(UserException.class, () -> useCase.update(userId, updateData));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.USER0001.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.USER0001.getMessage());

        verify(userQueryGateway).findById(userId);
        verifyNoInteractions(userUpdateGateway);
    }

    @Test
    void givenEmailAlreadyUsedByAnotherUser_whenUpdate_thenThrowsUserExceptionUSER0002() throws UserException {
        // Given: existing user and another user with same email
        User existing = new User();
        existing.setId(userId);
        existing.setEmail("old@example.com");
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(existing));

        User updateData = new User();
        updateData.setEmail("new@example.com");
        User another = new User();
        another.setId(UUID.randomUUID());
        when(userQueryGateway.findByEmail("new@example.com")).thenReturn(Optional.of(another));

        // When + Then: expect USER0002
        UserException ex = assertThrows(UserException.class, () -> useCase.update(userId, updateData));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.USER0002.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.USER0002.getMessage());

        verify(userQueryGateway).findById(userId);
        verify(userQueryGateway).findByEmail("new@example.com");
        verifyNoInteractions(userUpdateGateway);
    }

    @Test
    void givenGatewayFailsUpdate_whenUpdate_thenThrowsUserExceptionSYS0001() throws UserException {
        // Given: existing user, update data with valid password, gateway fails
        User existing = new User();
        existing.setId(userId);
        existing.setEmail("test@example.com");
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(existing));

        User updateData = new User();
        updateData.setName("New Name");
        updateData.setPassword("Aa1@abcd"); // valid password

        when(userUpdateGateway.updateUser(eq(userId), any(User.class))).thenReturn(false);

        // When + Then: expect SYS0001
        UserException ex = assertThrows(UserException.class, () -> useCase.update(userId, updateData));
        assertThat(ex.getCode()).isEqualTo(ErrorCodeEnum.SYS0001.getCode());
        assertThat(ex.getMessage()).isEqualTo(ErrorCodeEnum.SYS0001.getMessage());

        verify(userQueryGateway).findById(userId);
        verify(userUpdateGateway).updateUser(eq(userId), userCaptor.capture());
    }

    @Test
    void givenValidUpdate_whenUpdate_thenReturnsUpdatedUser() throws UserException {
        // Given: existing user, unique new email, gateway succeeds
        User existing = new User();
        existing.setId(userId);
        existing.setEmail("old@example.com");
        when(userQueryGateway.findById(userId)).thenReturn(Optional.of(existing));

        User updateData = new User();
        updateData.setEmail("new@example.com");
        updateData.setName("New Name");
        updateData.setPassword("Aa1@abcd"); // valid password

        when(userQueryGateway.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userUpdateGateway.updateUser(eq(userId), any(User.class))).thenReturn(true);

        // When
        User result = useCase.update(userId, updateData);

        // Then: verify delegation and updated fields
        verify(userQueryGateway).findById(userId);
        verify(userQueryGateway).findByEmail("new@example.com");
        verify(userUpdateGateway).updateUser(eq(userId), userCaptor.capture());
        User captured = userCaptor.getValue();
        assertThat(captured.getEmail()).isEqualTo("new@example.com");
        assertThat(captured.getName()).isEqualTo("New Name");
        assertThat(captured.getPassword()).isEqualTo("Aa1@abcd");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getPassword()).isEqualTo("Aa1@abcd");
    }
}
