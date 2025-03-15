package br.com.gerenciador.infrastructure.dto.response;

public record ValidationError(String field, String message) {
}