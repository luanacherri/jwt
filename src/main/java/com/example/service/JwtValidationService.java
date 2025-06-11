package com.example.service;

import com.example.service.validator.*;
import com.example.service.decoder.JwtDecoder;
import com.example.service.exception.JwtValidationException;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class JwtValidationService implements JwtValidatorService {
    
    private final JwtDecoder jwtDecoder;
    private final List<ClaimValidator> validators;

    public JwtValidationService(JwtDecoder jwtDecoder, ClaimValidatorFactory validatorFactory) {
        this.jwtDecoder = jwtDecoder;
        this.validators = validatorFactory.createValidators();
    }

    @Override
    public boolean validateJwt(String jwt) {
        try {
            // Decodificar o JWT
            Map<String, Object> claims = jwtDecoder.decode(jwt);
            
            // Verificar se contém exatamente as claims necessárias
            if (claims.size() != validators.size()) {
                throw new JwtValidationException(
                    String.format("JWT deve conter exatamente %d claims (Name, Role, Seed)", validators.size())
                );
            }

            // Validar cada claim usando seu validador específico
            for (ClaimValidator validator : validators) {
                if (!claims.containsKey(validator.getClaimName())) {
                    throw new JwtValidationException(
                        String.format("Claim '%s' está faltando", validator.getClaimName())
                    );
                }
                if (!validator.isValid(claims.get(validator.getClaimName()))) {
                    throw new JwtValidationException(
                        String.format("Claim '%s' é inválido", validator.getClaimName())
                    );
                }
            }

            return true;

        } catch (JwtValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtValidationException("Erro ao validar JWT: " + e.getMessage());
        }
    }
}
