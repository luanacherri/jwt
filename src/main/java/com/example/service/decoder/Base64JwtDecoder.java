package com.example.service.decoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Map;

@Component
public class Base64JwtDecoder implements JwtDecoder {
    
    private final ObjectMapper objectMapper;

    public Base64JwtDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> decode(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                throw new JwtDecodingException("JWT inválido: formato incorreto");
            }

            String payload;
            try {
                payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            } catch (IllegalArgumentException e) {
                throw new JwtDecodingException("JWT inválido: payload não está em Base64 válido", e);
            }

            try {
                return objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                throw new JwtDecodingException("JWT inválido: payload não é um JSON válido", e);
            }
        } catch (JwtDecodingException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtDecodingException("Erro ao decodificar JWT", e);
        }
    }
}
