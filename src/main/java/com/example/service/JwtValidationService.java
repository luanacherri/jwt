package com.example.service;

import com.example.service.validator.*;
import com.example.service.decoder.JwtDecoder;
import com.example.service.exception.JwtValidationException;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import org.slf4j.MDC;

@Service
@Observed(name = "jwt.service", contextualName = "jwt-validation-service")
public class JwtValidationService implements JwtValidatorService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtValidationService.class);
    private final JwtDecoder jwtDecoder;
    private final List<ClaimValidator> validators;
    private final Tracer tracer;

    public JwtValidationService(JwtDecoder jwtDecoder, ClaimValidatorFactory validatorFactory, Tracer tracer) {
        this.jwtDecoder = jwtDecoder;
        this.validators = validatorFactory.createValidators();
        this.tracer = tracer;
        logger.info("JwtValidationService initialized with {} validators", validators.size());
    }

    @Override
    @Observed(name = "jwt.validate", contextualName = "validate-jwt-service")
    public boolean validateJwt(String jwt) {
        String requestId = MDC.get("requestId");
        Context context = Context.current();
        
        Span span = tracer.spanBuilder("validateJwtService")
            .setParent(context)
            .startSpan();
        
        try {
            span.setAttribute("requestId", requestId != null ? requestId : "none");
            logger.debug("Starting JWT validation [requestId={}]", requestId);
            
            // Decodificar o JWT
            logger.trace("Attempting to decode JWT [requestId={}]", requestId);
            Map<String, Object> claims = jwtDecoder.decode(jwt);
            
            logger.debug("JWT decoded successfully, found {} claims [requestId={}]", claims.size(), requestId);
            span.setAttribute("claims.count", String.valueOf(claims.size()));
            
            // Verificar se contém exatamente as claims necessárias
            if (claims.size() != validators.size()) {
                String errorMsg = String.format("JWT deve conter exatamente %d claims (Name, Role, Seed)", validators.size());
                span.setStatus(StatusCode.ERROR, errorMsg);
                throw new JwtValidationException(errorMsg);
            }

            // Validar cada claim usando seu validador específico
            for (ClaimValidator validator : validators) {
                String claimName = validator.getClaimName();
                
                if (!claims.containsKey(claimName)) {
                    String errorMsg = String.format("Claim '%s' está faltando", claimName);
                    span.setStatus(StatusCode.ERROR, errorMsg);
                    throw new JwtValidationException(errorMsg);
                }
                
                Object claimValue = claims.get(claimName);
                if (!validator.isValid(claimValue)) {
                    String errorMsg = String.format("Claim '%s' é inválido", claimName);
                    span.setStatus(StatusCode.ERROR, errorMsg);
                    throw new JwtValidationException(errorMsg);
                }
                
                span.setAttribute("claim." + claimName, String.valueOf(claimValue));
            }

            span.setStatus(StatusCode.OK);
            return true;

        } catch (JwtValidationException e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            throw e;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, "Unexpected error");
            span.recordException(e);
            throw new JwtValidationException("Erro ao validar JWT: " + e.getMessage());
        } finally {
            span.end();
        }
    }
}
