package com.example.service.decoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Map;
import org.slf4j.MDC;

@Component
public class Base64JwtDecoder implements JwtDecoder {
    
    private static final Logger logger = LoggerFactory.getLogger(Base64JwtDecoder.class);
    private final ObjectMapper objectMapper;

    public Base64JwtDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        logger.info("Base64JwtDecoder initialized");
    }

    @Override
    public Map<String, Object> decode(String jwt) {
        String requestId = MDC.get("requestId");
        logger.debug("Starting JWT decoding [requestId={}]", requestId);
        
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                logger.warn("Invalid JWT format: insufficient parts [requestId={}]", requestId);
                throw new JwtDecodingException("JWT inválido: formato incorreto");
            }

            String payload;
            try {
                logger.trace("Decoding Base64 payload [requestId={}]", requestId);
                payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            } catch (IllegalArgumentException e) {
                logger.warn("Failed to decode Base64 payload [requestId={}]", requestId, e);
                throw new JwtDecodingException("JWT inválido: payload não está em Base64 válido", e);
            }

            try {
                logger.trace("Parsing JSON payload [requestId={}]", requestId);
                Map<String, Object> claims = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
                logger.debug("Successfully decoded JWT with {} claims [requestId={}]", claims.size(), requestId);
                return claims;
            } catch (Exception e) {
                logger.warn("Failed to parse JSON payload [requestId={}]", requestId, e);
                throw new JwtDecodingException("JWT inválido: payload não é um JSON válido", e);
            }
        } catch (JwtDecodingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during JWT decoding [requestId={}]", requestId, e);
            throw new JwtDecodingException("Erro ao decodificar JWT", e);
        }
    }
}
