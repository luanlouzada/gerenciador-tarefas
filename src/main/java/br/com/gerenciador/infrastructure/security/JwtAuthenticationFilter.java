package br.com.gerenciador.infrastructure.security;

import static br.com.gerenciador.infrastructure.utils.Utilities.log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.gerenciador.application.gateway.user.UserQueryGateway;
import br.com.gerenciador.domain.exception.UserException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserQueryGateway userQueryGateway;

    public JwtAuthenticationFilter(JwtService jwtService, UserQueryGateway userQueryGateway) {
        this.jwtService = jwtService;
        this.userQueryGateway = userQueryGateway;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userIdStr;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userIdStr = jwtService.extractUserId(jwt);

        if (userIdStr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtService.isTokenValid(jwt)) {
                    UUID userId = UUID.fromString(userIdStr);

                    // Se precisar verificar se o usuário existe no banco
                    var userOptional = userQueryGateway.findById(userId);
                    if (userOptional.isPresent()) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                new ArrayList<>());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        // Adiciona o ID do usuário como atributo da requisição para uso nos controllers
                        request.setAttribute("userId", userId);
                    }
                }
            } catch (UserException | IllegalArgumentException e) {
                log.error("Erro ao processar token JWT: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}