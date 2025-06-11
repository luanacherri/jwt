package com.example.service;

/**
 * Interface para validação de JWT tokens
 */
public interface JwtValidatorService {
    /**
     * Valida um JWT token de acordo com as regras de negócio
     * @param jwt o token JWT a ser validado
     * @return true se o token é válido, false caso contrário
     */
    boolean validateJwt(String jwt);
}
