package br.com.gerenciador.infrastructure.websocket;

import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.infrastructure.security.JwtService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final JwtService jwtService;
    private final UserQueryGateway userQueryGateway;

    public WebSocketConfig(NotificationWebSocketHandler notificationWebSocketHandler,
                           JwtService jwtService,
                           UserQueryGateway userQueryGateway) {
        this.notificationWebSocketHandler = notificationWebSocketHandler;
        this.jwtService = jwtService;
        this.userQueryGateway = userQueryGateway;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationWebSocketHandler, "/ws/notifications")
                .setAllowedOrigins("http://localhost:3000")
                .addInterceptors(new WebSocketAuthHandshakeInterceptor(jwtService, userQueryGateway))
                .withSockJS()
                .setSuppressCors(false)
                .setWebSocketEnabled(true);
    }

    @Bean
    @Lazy
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "spring.websocket.enabled", havingValue = "true", matchIfMissing = true)
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}