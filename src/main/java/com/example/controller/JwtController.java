package com.example.controller;

import com.example.controller.dto.JwtValidationRequest;
import com.example.controller.dto.JwtValidationResponse;
import com.example.service.JwtValidationService;
import com.example.service.exception.JwtValidationException;
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

@RestController
@RequestMapping("/api/v1/jwt")
@Tag(name = "JWT", description = "API para validação de JWTs")
@CrossOrigin
public class JwtController {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtController.class);
    private final JwtValidationService jwtValidationService;

    public JwtController(JwtValidationService jwtValidationService) {
        this.jwtValidationService = jwtValidationService;
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
    public ResponseEntity<JwtValidationResponse> validateJwt(
            @Valid @RequestBody JwtValidationRequest request,
            WebRequest webRequest) {
        
        logger.debug("Validando JWT via POST");
        try {
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

            return ResponseEntity.ok(response);

        } catch (JwtValidationException e) {
            logger.warn("Erro de validação do JWT: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(JwtValidationResponse.builder()
                    .valid(false)
                    .type("https://api.validator.com/problems/invalid-jwt")
                    .title("JWT Inválido")
                    .detail(e.getMessage())
                    .instance(webRequest.getDescription(false))
                    .build());

        } catch (Exception e) {
            logger.error("Erro inesperado ao validar JWT", e);
            return ResponseEntity.internalServerError()
                .body(JwtValidationResponse.builder()
                    .valid(false)
                    .type("https://api.validator.com/problems/internal-error")
                    .title("Erro Interno")
                    .detail("Ocorreu um erro inesperado ao validar o JWT")
                    .instance(webRequest.getDescription(false))
                    .build());
        }
    }
}
