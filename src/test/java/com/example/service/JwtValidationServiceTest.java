package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class JwtValidationServiceTest {

    private JwtValidationService jwtValidationService;

    @BeforeEach
    void setUp() {
        jwtValidationService = new JwtValidationService();
    }

    @Test
    void testJwtValido() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        assertTrue(jwtValidationService.validateJwt(jwt), "JWT válido deve retornar true");
    }

    @Test
    void testJwtInvalido() {
        String jwt = "eyJhbGciOiJzI1NiJ9.dfsdfsfryJSr2xrIjoiQWRtaW4iLCJTZrkIjoiNzg0MSIsIk5hbrUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05fsdfsIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        assertFalse(jwtValidationService.validateJwt(jwt), "JWT inválido deve retornar false");
    }

    @Test
    void testNomeComNumeros() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiRXh0ZXJuYWwiLCJTZWVkIjoiODgwMzciLCJOYW1lIjoiTTRyaWEgT2xpdmlhIn0.6YD73XWZYQSSMDf6H0i3-kylz1-TY_Yt6h1cV2Ku-Qs";
        assertFalse(jwtValidationService.validateJwt(jwt), "JWT com nome contendo números deve retornar false");
    }

    @Test
    void testMaisDeTresClaims() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiTWVtYmVyIiwiT3JnIjoiQlIiLCJTZWVkIjoiMTQ2MjciLCJOYW1lIjoiVmFsZGlyIEFyYW5oYSJ9.cmrXV_Flm5mfdpfNUVopY_I2zeJUy4EZ4i3Fea98zvY";
        assertFalse(jwtValidationService.validateJwt(jwt), "JWT com mais de 3 claims deve retornar false");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg", // Role: Admin
        "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiTWVtYmVyIiwiU2VlZCI6Ijc4NDEiLCJOYW1lIjoiVG9uaW5obyBBcmF1am8ifQ.invalid", // Role: Member
        "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiRXh0ZXJuYWwiLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.invalid" // Role: External
    })
    void testRolesValidos(String jwt) {
        assertTrue(jwtValidationService.validateJwt(jwt), "JWT com roles válidos devem retornar true");
    }

    @Test
    void testRoleInvalido() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiSW52YWxpZFJvbGUiLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.invalid";
        assertFalse(jwtValidationService.validateJwt(jwt), "JWT com role inválido deve retornar false");
    }

    @Test
    void testNomeMuitoLongo() {
        StringBuilder nomeLongo = new StringBuilder();
        for (int i = 0; i < 257; i++) {
            nomeLongo.append("a");
        }
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiI" + nomeLongo.toString() + "In0.invalid";
        assertFalse(jwtValidationService.validateJwt(jwt), "JWT com nome muito longo deve retornar false");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNyIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.invalid", // 7 (primo)
        "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiMTEiLCJOYW1lIjoiVG9uaW5obyBBcmF1am8ifQ.invalid", // 11 (primo)
        "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiMTMiLCJOYW1lIjoiVG9uaW5obyBBcmF1am8ifQ.invalid" // 13 (primo)
    })
    void testSeedsPrimos(String jwt) {
        assertTrue(jwtValidationService.validateJwt(jwt), "JWT com seeds primos devem retornar true");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNCIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.invalid", // 4 (não primo)
        "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNiIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.invalid", // 6 (não primo)
        "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiOSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.invalid" // 9 (não primo)
    })
    void testSeedsNaoPrimos(String jwt) {
        assertFalse(jwtValidationService.validateJwt(jwt), "JWT com seeds não primos devem retornar false");
    }
}
