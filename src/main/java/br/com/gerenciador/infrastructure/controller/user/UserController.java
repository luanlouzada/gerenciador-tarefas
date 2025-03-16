package br.com.gerenciador.infrastructure.controller.user;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.infrastructure.dto.request.user.LoginUserRequest;
import br.com.gerenciador.infrastructure.dto.request.user.RegistrationUserRequest;
import br.com.gerenciador.infrastructure.dto.response.AuthResponse;
import br.com.gerenciador.infrastructure.dto.response.BaseResponse;
import br.com.gerenciador.infrastructure.mapper.UserMapper;
import br.com.gerenciador.infrastructure.security.JwtService;
import br.com.gerenciador.usecase.user.UserAuthenticationUseCase;
import br.com.gerenciador.usecase.user.UserRegistrationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/user")
@Tag(name = "User", description = "Endpoints for user management")
public class UserController {
    private final UserRegistrationUseCase userRegistrationUseCase;
    private final UserAuthenticationUseCase userAuthenticationUseCase;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public UserController(UserRegistrationUseCase userRegistrationUseCase,
            UserAuthenticationUseCase userAuthenticationUseCase, UserMapper userMapper, JwtService jwtService) {
        this.userRegistrationUseCase = userRegistrationUseCase;
        this.userAuthenticationUseCase = userAuthenticationUseCase;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/registerUser")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse<String>> registerUser(@Valid @RequestBody RegistrationUserRequest request)
            throws Exception {
        log.info("Inicio da criação do usuário::UserController");
        userRegistrationUseCase.registerUser(userMapper.toUser(request));
        log.info("Usuário criado com sucesso::UserController");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<String>builder().success(true).message("Usuário criado com sucesso").build());
    }

    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginUserRequest request)
            throws UserException {
        log.info("Inicio do processo de login::UserController");
        UUID userId = userAuthenticationUseCase.authenticate(request.email(), request.password());
        String jwtToken = jwtService.generateToken(userId);
        log.info("Login realizado com sucesso::UserController");

        return ResponseEntity.ok(BaseResponse.<AuthResponse>builder()
                .result(new AuthResponse(jwtToken, userId))
                .success(true)
                .message("Login realizado com sucesso")
                .build());
    }
}