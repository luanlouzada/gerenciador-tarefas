package br.com.gerenciador.infrastructure.dto.response;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId) {
}