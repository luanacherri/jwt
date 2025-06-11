package com.example.service;

import com.example.service.decoder.JwtDecoder;
import com.example.service.decoder.JwtDecodingException;
import com.example.service.exception.JwtValidationException;
import com.example.service.validator.ClaimValidatorFactory;
import com.example.service.validator.DefaultClaimValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

class JwtValidationServiceTest {

    @Mock
    private JwtDecoder jwtDecoder;
    
    private JwtValidationService jwtValidationService;
    private ClaimValidatorFactory validatorFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validatorFactory = new DefaultClaimValidatorFactory();
        jwtValidationService = new JwtValidationService(jwtDecoder, validatorFactory);
    }

    @Test
    void testJwtValido() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Role", "Admin");
        claims.put("Seed", "7841");
        claims.put("Name", "Toninho Araujo");

        when(jwtDecoder.decode(anyString())).thenReturn(claims);

        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        assertTrue(jwtValidationService.validateJwt(jwt), "JWT válido deve ser aceito");
    }

    @Test
    void testJwtInvalido() {
        String jwt = "eyJhbGciOiJzI1NiJ9.dfsdfsfryJSr2xrIjoiQWRtaW4iLCJTZrkIjoiNzg0MSIsIk5hbrUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05fsdfsIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        
        doThrow(new JwtDecodingException("JWT inválido: payload não é um JSON válido"))
            .when(jwtDecoder)
            .decode(anyString());

        JwtValidationException exception = assertThrows(
            JwtValidationException.class,
            () -> jwtValidationService.validateJwt(jwt),
            "JWT inválido deve lançar exceção"
        );
        assertEquals("JWT inválido: payload não é um JSON válido", exception.getMessage());
    }

    @Test
    void testNomeComNumeros() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Role", "External");
        claims.put("Seed", "88037");
        claims.put("Name", "M4ria Olivia");

        when(jwtDecoder.decode(anyString())).thenReturn(claims);

        String jwt = "qualquer.jwt.invalido";
        JwtValidationException exception = assertThrows(
            JwtValidationException.class,
            () -> jwtValidationService.validateJwt(jwt),
            "JWT com nome contendo números deve lançar exceção"
        );
        assertEquals("Claim 'Name' é inválido", exception.getMessage());
    }

    @Test
    void testMaisDeTresClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Role", "Member");
        claims.put("Org", "BR");
        claims.put("Seed", "14627");
        claims.put("Name", "Valdir Aranha");

        when(jwtDecoder.decode(anyString())).thenReturn(claims);

        String jwt = "qualquer.jwt.invalido";
        JwtValidationException exception = assertThrows(
            JwtValidationException.class,
            () -> jwtValidationService.validateJwt(jwt),
            "JWT com mais de 3 claims deve lançar exceção"
        );
        assertTrue(exception.getMessage().contains("JWT deve conter exatamente 3 claims"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Admin", "Member", "External"
    })
    void testRolesValidos(String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Role", role);
        claims.put("Seed", "7841");
        claims.put("Name", "Toninho Araujo");

        when(jwtDecoder.decode(anyString())).thenReturn(claims);

        String jwt = "qualquer.jwt.valido";
        assertTrue(jwtValidationService.validateJwt(jwt), "JWT com roles válidos devem ser aceitos");
    }

    @Test
    void testRoleInvalido() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Role", "InvalidRole");
        claims.put("Seed", "7841");
        claims.put("Name", "Toninho Araujo");

        when(jwtDecoder.decode(anyString())).thenReturn(claims);

        String jwt = "qualquer.jwt.invalido";
        JwtValidationException exception = assertThrows(
            JwtValidationException.class,
            () -> jwtValidationService.validateJwt(jwt),
            "JWT com role inválido deve lançar exceção"
        );
        assertEquals("Claim 'Role' é inválido", exception.getMessage());
    }

    @Test
    void testNomeMuitoLongo() {
        StringBuilder nomeLongo = new StringBuilder();
        for (int i = 0; i < 257; i++) {
            nomeLongo.append("a");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("Role", "Admin");
        claims.put("Seed", "7841");
        claims.put("Name", nomeLongo.toString());

        when(jwtDecoder.decode(anyString())).thenReturn(claims);
        
        String jwt = "qualquer.jwt.invalido";
        JwtValidationException exception = assertThrows(
            JwtValidationException.class,
            () -> jwtValidationService.validateJwt(jwt),
            "JWT com nome muito longo deve lançar exceção"
        );
        assertEquals("Claim 'Name' é inválido", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "7", "11", "13"  // números primos
    })
    void testSeedsPrimos(String seed) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Role", "Admin");
        claims.put("Seed", seed);
        claims.put("Name", "Toninho Araujo");

        when(jwtDecoder.decode(anyString())).thenReturn(claims);

        String jwt = "qualquer.jwt.valido";
        assertTrue(jwtValidationService.validateJwt(jwt), "JWT com seeds primos devem ser aceitos");
    }

    @Test
    void testSeedsNaoPrimos() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Role", "Admin");
        claims.put("Seed", "4");  // não primo
        claims.put("Name", "Toninho Araujo");

        when(jwtDecoder.decode(anyString())).thenReturn(claims);

        String jwt = "qualquer.jwt.invalido";
        JwtValidationException exception = assertThrows(
            JwtValidationException.class,
            () -> jwtValidationService.validateJwt(jwt),
            "JWT com seed não primo deve lançar exceção"
        );
        assertEquals("Claim 'Seed' é inválido", exception.getMessage());
    }
}
