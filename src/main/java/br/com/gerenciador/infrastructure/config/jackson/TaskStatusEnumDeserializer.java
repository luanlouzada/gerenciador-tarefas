package br.com.gerenciador.infrastructure.config.jackson;

import br.com.gerenciador.domain.enums.TaskStatusEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TaskStatusEnumDeserializer extends JsonDeserializer<TaskStatusEnum> {

    @Override
    public TaskStatusEnum deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String value = jsonParser.getText();

        try {
            return TaskStatusEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ignored) {

        }

        for (TaskStatusEnum status : TaskStatusEnum.values()) {
            if (status.getStatus().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + value);
    }
}