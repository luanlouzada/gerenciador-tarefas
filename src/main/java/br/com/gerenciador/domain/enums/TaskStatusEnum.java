package br.com.gerenciador.domain.enums;

public enum TaskStatusEnum {
    IN_PROGRESS("Em andamento"),
    PENDING("Pendente"),
    DRAFT("Rascunho"),
    COMPLETED("Concluído"),
    EXPIRED("Expirado");

    private final String status;

    TaskStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
