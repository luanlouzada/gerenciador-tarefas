package br.com.gerenciador.domain.exception;

public class TaskException extends Exception {

    private String code;

    public TaskException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}