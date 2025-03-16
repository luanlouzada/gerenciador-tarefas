package br.com.gerenciador.infrastructure.dto.request.task;

import br.com.gerenciador.domain.enums.TaskStatusEnum;
import br.com.gerenciador.infrastructure.config.jackson.TaskStatusEnumDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TaskUpdateRequest(
        @Schema(description = "Título da tarefa (mínimo 20 caracteres)", example = "Completar documentação do projeto")
        @NotBlank String title,

        @Schema(description = "Descrição da tarefa", example = "Escrever toda a documentação necessária para o projeto")
        @NotBlank String description,

        @Schema(description = "Data de vencimento da tarefa no formato dd/MM/yyyy HH:mm", example = "31/12/2023 23:59")
        @NotBlank String dueAt,

        @Schema(description = "Status da tarefa (em inglês: IN_PROGRESS, COMPLETED, EXPIRED, PENDING, DRAFT; " +
                "ou em português: Em andamento, Concluído, Expirado, Pendente, Rascunho)",
                example = "Concluído")
        @JsonDeserialize(using = TaskStatusEnumDeserializer.class)
        TaskStatusEnum status) {
}