package br.com.gerenciador.infrastructure.websocket;

import br.com.gerenciador.domain.model.Task;
import br.com.gerenciador.usecase.user.UserQueryUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserQueryUseCase userQueryUseCase;

    public NotificationWebSocketHandler(UserQueryUseCase userQueryUseCase) {
        this.userQueryUseCase = userQueryUseCase;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Nova conexão WebSocket estabelecida: {}", session.getId());

        Map<String, Object> attributes = session.getAttributes();
        if (attributes.containsKey("SPRING_SECURITY_CONTEXT")) {
            Authentication auth = (Authentication) attributes.get("SPRING_SECURITY_CONTEXT");
            if (auth != null) {
                String userId = auth.getPrincipal().toString();
                userSessions.put(userId, session);
                log.info("Sessão WebSocket associada ao usuário com ID: {}", userId);
                log.debug("Sessões ativas após nova conexão: {}", userSessions.keySet());
            }
        } else {
            log.warn("Conexão WebSocket sem autenticação: {}", session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        log.info("Conexão WebSocket fechada: {} - Status: {}", session.getId(), status);

        Principal principal = session.getPrincipal();
        if (principal != null) {
            String userId = principal.getName();
            userSessions.remove(userId);
            log.info("Sessão WebSocket removida para o usuário com ID: {}", userId);
        }
    }

    public void sendNotification(Task task) {
        try {
            if (task.getUserId() == null) {
                log.warn("Tarefa sem usuário associado: {}", task.getTitle());
                return;
            }

            String userId = task.getUserId().toString();
            log.debug("Tentando enviar notificação para usuário com ID: {}", userId);

            log.debug("Sessões ativas: {}", userSessions.keySet());

            WebSocketSession session = userSessions.get(userId);
            if (session == null || !session.isOpen()) {
                log.info("Usuário {} não está conectado via WebSocket", userId);
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dueAt = task.getDueAt();
            long minutesRemaining = java.time.temporal.ChronoUnit.MINUTES.between(now, dueAt);

            String message;
            if (minutesRemaining > 1) {
                message = String.format("Sua tarefa '%s' vence em %d minutos",
                        task.getTitle(), minutesRemaining);
            } else {
                message = String.format("Sua tarefa '%s' está prestes a vencer!", task.getTitle());
            }

            TextMessage textMessage = new TextMessage(message);
            session.sendMessage(textMessage);
            log.info("Notificação WebSocket enviada para usuário {}: {}", userId, message);

        } catch (Exception e) {
            log.error("Erro ao enviar notificação sobre tarefa: {}", task.getTitle(), e);
        }
    }
}