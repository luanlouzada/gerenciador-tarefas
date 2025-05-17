package br.com.gerenciador.infrastructure.controller.user;

import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.model.User;
import br.com.gerenciador.infrastructure.dto.request.user.LoginUserRequest;
import br.com.gerenciador.infrastructure.dto.request.user.RegistrationUserRequest;
import br.com.gerenciador.infrastructure.dto.response.AuthResponse;
import br.com.gerenciador.infrastructure.dto.response.BaseResponse;
import br.com.gerenciador.infrastructure.mapper.UserMapper;
import br.com.gerenciador.infrastructure.security.JwtService;
import br.com.gerenciador.usecase.user.UserAuthenticationUseCase;
import br.com.gerenciador.usecase.user.UserRegistrationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserRegistrationUseCase userRegistrationUseCase;

    @Mock
    private UserAuthenticationUseCase userAuthenticationUseCase;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController controller;

    @Test
    void givenValidRegistrationRequest_whenRegisterUser_thenReturnsCreatedResponse() throws Exception {
        // Given
        String email = "test@example.com";
        String name = "Test User";
        String password = "Password123!";
        RegistrationUserRequest request = new RegistrationUserRequest(email, name, password);
        User mappedUser = new User(name, password, email);
        when(userMapper.toUser(request)).thenReturn(mappedUser);
        doNothing().when(userRegistrationUseCase).registerUser(mappedUser);

        // When
        ResponseEntity<BaseResponse<String>> response = controller.registerUser(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        BaseResponse<String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getSuccess()).isTrue();
        assertThat(body.getMessage()).isEqualTo("Usuário criado com sucesso");
        verify(userMapper).toUser(request);
        verify(userRegistrationUseCase).registerUser(mappedUser);
    }

    @Test
    void givenExistingUser_whenRegisterUser_thenThrowsUserException() throws Exception {
        // Given
        String email = "existing@example.com";
        String name = "Existing User";
        String password = "Password123!";
        RegistrationUserRequest request = new RegistrationUserRequest(email, name, password);
        User mappedUser = new User(name, password, email);
        when(userMapper.toUser(request)).thenReturn(mappedUser);
        doThrow(new UserException("User already exists", "USER0002"))
                .when(userRegistrationUseCase).registerUser(mappedUser);

        // When / Then
        assertThrows(UserException.class, () -> controller.registerUser(request));
        verify(userMapper).toUser(request);
        verify(userRegistrationUseCase).registerUser(mappedUser);
    }

    @Test
    void givenValidLoginRequest_whenLogin_thenReturnsAuthToken() throws UserException {
        // Given
        String email = "user@test.com";
        String password = "Pwd12345";
        LoginUserRequest request = new LoginUserRequest(email, password);
        UUID userId = UUID.randomUUID();
        String token = "jwt.token.value";
        when(userAuthenticationUseCase.authenticate(email, password)).thenReturn(userId);
        when(jwtService.generateToken(userId)).thenReturn(token);

        // When
        ResponseEntity<BaseResponse<AuthResponse>> response = controller.login(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        BaseResponse<AuthResponse> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getSuccess()).isTrue();
        AuthResponse result = body.getResult();
        assertThat(result.token()).isEqualTo(token);
        assertThat(result.userId()).isEqualTo(userId);
        verify(userAuthenticationUseCase).authenticate(email, password);
        verify(jwtService).generateToken(userId);
    }

    @Test
    void givenAuthenticationFails_whenLogin_thenThrowsUserException() throws UserException {
        // Given
        String email = "bad@user.com";
        String password = "wrong";
        LoginUserRequest request = new LoginUserRequest(email, password);
        when(userAuthenticationUseCase.authenticate(anyString(), anyString()))
                .thenThrow(new UserException("Invalid credentials", "AUTH0001"));

        // When / Then
        assertThrows(UserException.class, () -> controller.login(request));
        verify(userAuthenticationUseCase).authenticate(email, password);
        verifyNoInteractions(jwtService);
    }
}
