package br.com.gerenciador.infrastructure.websocket;

import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.infrastructure.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthHandshakeInterceptor.class);
    private final JwtService jwtService;
    private final UserQueryGateway userQueryGateway;

    public WebSocketAuthHandshakeInterceptor(JwtService jwtService, UserQueryGateway userQueryGateway) {
        this.jwtService = jwtService;
        this.userQueryGateway = userQueryGateway;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String uri = request.getURI().toString();
        String token = extractTokenFromUri(uri);

        if (token != null) {
            try {
                String userIdStr = jwtService.extractUserId(token);
                if (userIdStr != null && jwtService.isTokenValid(token)) {
                    UUID userId = UUID.fromString(userIdStr);
                    var userOptional = userQueryGateway.findById(userId);

                    if (userOptional.isPresent()) {
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                userId.toString(), null, new ArrayList<>());
                        attributes.put("SPRING_SECURITY_CONTEXT", auth);
                        log.info("Usuário autenticado no WebSocket: {}", userId);
                        return true;
                    }
                }
            } catch (Exception e) {
                log.error("Erro ao processar token para WebSocket", e);
            }
        }

        log.warn("Falha na autenticação do WebSocket");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {

    }

    private String extractTokenFromUri(String uri) {
        // Formato esperado: /ws/notifications?token=xxx
        if (uri.contains("token=")) {
            String[] parts = uri.split("token=");
            if (parts.length > 1) {
                String tokenWithPossibleExtra = parts[1];

                if (tokenWithPossibleExtra.contains("&")) {
                    return tokenWithPossibleExtra.split("&")[0];
                }
                return tokenWithPossibleExtra;
            }
        }
        return null;
    }
}