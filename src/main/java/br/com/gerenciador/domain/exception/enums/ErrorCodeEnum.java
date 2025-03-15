package br.com.gerenciador.domain.exception.enums;

public enum ErrorCodeEnum {
    USER0001("Usuário não encontrado", "USER-0001"),
    USER0002("Email já cadastrado", "USER-0002"),
    USER0003("Senha inválida", "USER-0003"),
    USER0004("Houve um erro na criação do usuário", "USER-0004"),
    USER0005("Dados de usuário inválidos", "USER-0005"),
    USER0006("Nome não pode ser vazio", "USER-0006"),
    USER0007("Nome deve ter pelo menos 3 caracteres", "USER-0007"),
    USER0008("A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial", "USER-0008"),
    USER0009("A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial", "USER-0009"),
    USER0010("Email não pode ser vazio", "USER-0010"),
    USER0011("Formato de email inválido", "USER-0011"),

    TASK0001("Tarefa não encontrada", "TASK-0001"),
    TASK0002("Usuário não autorizado para esta tarefa", "TASK-0002"),
    TASK0003("Houve um erro na criação da tarefa", "TASK-0003"),
    TASK0004("Data de vencimento inválida", "TASK-0004"),
    TASK0005("Status da tarefa inválido", "TASK-0005"),
    TASK0006("Descrição da tarefa muito longa", "TASK-0006"),
    TASK0007("O título deve ter pelo menos 20 caracteres", "TASK-0007"),
    TASK0008("Já existe uma tarefa com esse título", "TASK-0008"),

    AUTH0001("Falha na autenticação", "AUTH-0001"),
    AUTH0002("Token inválido ou expirado", "AUTH-0002"),
    AUTH0003("Usuário não autorizado para esta operação", "AUTH-0003"),

    SYS0001("Erro interno do servidor", "SYS-0001"),
    SYS0002("Requisição inválida", "SYS-0002"),
    SYS0003("Serviço temporariamente indisponível", "SYS-0003"),
    SYS0004("Invalid Request", "SYS-0004"),
    SYS0005("Erro ao processar tarefas próximas ao vencimento", "SYS-0005"),
    SYS0006("Erro ao processar tarefas vencidas", "SYS-0006");
    
    private String message;
    private String code;

    ErrorCodeEnum(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}