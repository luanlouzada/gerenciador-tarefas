package br.com.gerenciador.application.gateway.user;

public interface UserAuthenticationGateway {
    Boolean authenticate(String username, String password);
}
