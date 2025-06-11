package com.example.service;

/**
 * Interface para serviço de validação de JWT
 * Responsável por validar o formato e as claims de um JWT
 */
public interface JwtValidatorService {
    /**
     * Valida um JWT verificando seu formato e claims obrigatórios
     * 
     * @param jwt o token JWT a ser validado
     * @return true se o JWT for válido, false caso contrário
     * @throws com.example.service.exception.JwtValidationException se houver erro na validação
     */
    boolean validateJwt(String jwt);

    /**
     * Verifica se um JWT está expirado
     * 
     * @param jwt o token JWT a ser verificado
     * @return true se o JWT estiver expirado, false caso contrário
     * @throws com.example.service.exception.JwtValidationException se houver erro na validação
     */
    default boolean isExpired(String jwt) {
        throw new UnsupportedOperationException("Verificação de expiração não implementada");
    }

    /**
     * Extrai as claims de um JWT
     * 
     * @param jwt o token JWT
     * @return um mapa com as claims do JWT
     * @throws com.example.service.exception.JwtValidationException se houver erro na extração
     */
    default java.util.Map<String, Object> extractClaims(String jwt) {
        throw new UnsupportedOperationException("Extração de claims não implementada");
    }
}
