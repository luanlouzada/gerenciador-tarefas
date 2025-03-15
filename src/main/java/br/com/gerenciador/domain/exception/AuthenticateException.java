package br.com.gerenciador.domain.exception;

public class AuthenticateException extends Exception{

    private String code;

    public AuthenticateException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
