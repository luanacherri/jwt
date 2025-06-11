package com.example.service.decoder;

import java.util.Map;

public interface JwtDecoder {
    /**
     * Decodifica um JWT e retorna suas claims
     * @param jwt o token JWT
     * @return um mapa com as claims do token
     * @throws JwtDecodingException se houver erro na decodificação
     */
    Map<String, Object> decode(String jwt);
}
