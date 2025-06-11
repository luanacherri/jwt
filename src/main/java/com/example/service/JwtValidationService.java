package com.example.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Service
public class JwtValidationService {
    
    private static final List<String> VALID_ROLES = Arrays.asList("Admin", "Member", "External");
    private static final int MAX_NAME_LENGTH = 256;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean validateJwt(String jwt) {
        try {
            // Dividir o JWT em suas partes
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return false;

            // Decodificar o payload
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            // Converter para Map
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            
            // Verificar se contém exatamente as 3 claims necessárias
            if (claims.size() != 3 || !claims.containsKey("Name") || !claims.containsKey("Role") || !claims.containsKey("Seed")) {
                return false;
            }

            String name = String.valueOf(claims.get("Name"));
            String role = String.valueOf(claims.get("Role"));
            String seedStr = String.valueOf(claims.get("Seed"));

            // Converter a string do Seed para número
            Integer seed = null;
            try {
                seed = Integer.parseInt(seedStr);
            } catch (NumberFormatException e) {
                return false;
            }

            return validateName(name) &&
                   validateRole(role) &&
                   validateSeed(seed);

        } catch (Exception e) {
            System.out.println("Erro na validação: " + e.getMessage());
            return false;
        }
    }

    private boolean validateName(String name) {
        return name != null &&
               name.length() <= MAX_NAME_LENGTH &&
               !name.matches(".*\\d.*");
    }

    private boolean validateRole(String role) {
        return VALID_ROLES.contains(role);
    }

    private boolean validateSeed(Integer seed) {
        if (seed == null) return false;
        if (seed <= 1) return false;
        for (int i = 2; i <= Math.sqrt(seed); i++) {
            if (seed % i == 0) return false;
        }
        return true;
    }
}
