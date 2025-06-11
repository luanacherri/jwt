package com.example.service;

import com.example.service.validator.*;
import com.example.service.exception.JwtValidationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class JwtValidationService implements JwtValidatorService {
    
    private final ObjectMapper objectMapper;
    private final List<ClaimValidator> validators;

    public JwtValidationService() {
        this.objectMapper = new ObjectMapper();
        this.validators = Arrays.asList(
            new NameClaimValidator(),
            new RoleClaimValidator(),
            new SeedClaimValidator()
        );
    }

    @Override
    public boolean validateJwt(String jwt) {
        try {
            // Dividir o JWT em suas partes
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                throw new JwtValidationException("JWT inválido: formato incorreto");
            }

            // Decodificar o payload
            String payload;
            try {
                payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            } catch (IllegalArgumentException e) {
                throw new JwtValidationException("JWT inválido: payload não está em Base64 válido");
            }
            
            // Converter para Map
            Map<String, Object> claims;
            try {
                claims = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                throw new JwtValidationException("JWT inválido: payload não é um JSON válido");
            }
            
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
            throw e; // Re-throw validation exceptions
        } catch (Exception e) {
            throw new JwtValidationException("Erro ao validar JWT: " + e.getMessage());
        }
    }
}
