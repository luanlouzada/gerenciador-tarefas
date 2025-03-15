package br.com.gerenciador.infrastructure.config;

import br.com.gerenciador.application.gateway.user.UserAuthenticationGateway;
import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.application.gateway.user.UserRegistrationGateway;
import br.com.gerenciador.application.gateway.user.UserUpdateGateway;
import br.com.gerenciador.application.usecaseimpl.user.UserAuthenticationUseCaseImpl;
import br.com.gerenciador.application.usecaseimpl.user.UserQueryUseCaseImpl;
import br.com.gerenciador.application.usecaseimpl.user.UserRegistrationUseCaseImpl;
import br.com.gerenciador.application.usecaseimpl.user.UserUpdateUseCaseImpl;
import br.com.gerenciador.usecase.user.UserAuthenticationUseCase;
import br.com.gerenciador.usecase.user.UserQueryUseCase;
import br.com.gerenciador.usecase.user.UserRegistrationUseCase;
import br.com.gerenciador.usecase.user.UserUpdateUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    public UserUpdateUseCase userUpdateUseCase(UserUpdateGateway userUpdateGateway,
                                               UserQueryGateway userQueryGateway) {
        return new UserUpdateUseCaseImpl(userUpdateGateway, userQueryGateway);
    }

    @Bean
    public UserQueryUseCase userQueryUseCase(UserQueryGateway userQueryGateway) {
        return new UserQueryUseCaseImpl(userQueryGateway);
    }

    @Bean
    public UserAuthenticationUseCase userAuthenticationUseCase(UserAuthenticationGateway userAuthenticationGateway,
                                                               UserQueryGateway userQueryGateway) {
        return new UserAuthenticationUseCaseImpl(userAuthenticationGateway, userQueryGateway);
    }

    @Bean
    public UserRegistrationUseCase userRegistrationUseCase(UserRegistrationGateway userRegistrationGateway,
                                                           UserQueryGateway userQueryGateway) {
        return new UserRegistrationUseCaseImpl(userRegistrationGateway, userQueryGateway);
    }
}
