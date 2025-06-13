package com.example.controller;

import com.example.controller.dto.JwtValidationRequest;
import com.example.controller.dto.JwtValidationResponse;
import com.example.service.JwtValidationService;
import com.example.service.exception.JwtValidationException;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.MDC;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jwt")
@Tag(name = "JWT", description = "API para validação de JWTs")
@CrossOrigin
@Observed(name = "jwt.controller", contextualName = "jwt-validation-controller")
public class JwtController {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtController.class);
    private final JwtValidationService jwtValidationService;
    private final Tracer tracer;

    public JwtController(JwtValidationService jwtValidationService, Tracer tracer) {
        this.jwtValidationService = jwtValidationService;
        this.tracer = tracer;
        logger.info("JwtController initialized");
    }

    @Operation(
        summary = "Valida um JWT",
        description = "Valida um JSON Web Token (JWT) verificando seu formato e claims obrigatórios"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JWT válido"),
        @ApiResponse(responseCode = "400", description = "JWT inválido", 
            content = @Content(schema = @Schema(implementation = JwtValidationResponse.class)))
    })
    @PostMapping(
        path = "/validate",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Observed(name = "jwt.validate", contextualName = "validate-jwt")
    public ResponseEntity<JwtValidationResponse> validateJwt(
            @Valid @RequestBody JwtValidationRequest request,
            WebRequest webRequest) {
        
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        
        Span span = tracer.spanBuilder("validateJwt")
            .setAttribute("requestId", requestId)
            .setParent(Context.current())
            .startSpan();
        
        try {
            logger.info("Received JWT validation request [requestId={}]", requestId);
            span.setAttribute("jwt.length", request.getJwt().length());
            
            boolean isValid = jwtValidationService.validateJwt(request.getJwt());
            
            JwtValidationResponse response = isValid 
                ? JwtValidationResponse.valid() 
                : JwtValidationResponse.invalid("JWT inválido");
            
            response = JwtValidationResponse.builder()
                .valid(response.isValid())
                .type(response.getType())
                .title(response.getTitle())
                .detail(response.getDetail())
                .instance(webRequest.getDescription(false))
                .build();

            span.setAttribute("jwt.valid", response.isValid());
            span.setStatus(StatusCode.OK);
            
            logger.info("JWT validation completed - Result: {} [requestId={}]", 
                response.isValid() ? "VALID" : "INVALID", requestId);
            
            return ResponseEntity.ok(response);

        } catch (JwtValidationException e) {
            logger.warn("JWT validation failed: {} [requestId={}]", e.getMessage(), requestId);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            return ResponseEntity.badRequest()
                .body(JwtValidationResponse.builder()
                    .valid(false)
                    .type("https://api.validator.com/problems/invalid-jwt")
                    .title("JWT Inválido")
                    .detail(e.getMessage())
                    .instance(webRequest.getDescription(false))
                    .build());

        } catch (Exception e) {
            logger.error("Unexpected error during JWT validation [requestId={}]", requestId, e);
            span.setStatus(StatusCode.ERROR, "Internal error occurred");
            span.recordException(e);
            return ResponseEntity.internalServerError()
                .body(JwtValidationResponse.builder()
                    .valid(false)
                    .type("https://api.validator.com/problems/internal-error")
                    .title("Erro Interno")
                    .detail("Ocorreu um erro inesperado ao validar o JWT")
                    .instance(webRequest.getDescription(false))
                    .build());
        } finally {
            span.end();
            MDC.remove("requestId");
        }
    }
}
