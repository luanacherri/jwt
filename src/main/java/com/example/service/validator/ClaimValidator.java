package com.example.service.validator;

/**
 * Interface base para validadores de claims
 */
public interface ClaimValidator {
    /**
     * Valida uma claim específica
     * @param value o valor da claim a ser validado
     * @return true se a claim é válida, false caso contrário
     */
    boolean isValid(Object value);
    
    /**
     * Retorna o nome da claim que este validador processa
     * @return o nome da claim
     */
    String getClaimName();
}
