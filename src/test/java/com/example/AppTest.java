package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.service.JwtValidationService;

@SpringBootTest
public class AppTest {
    
    @Autowired
    private JwtValidationService jwtValidationService;

    @Test
    public void testCaso1_JWTValido() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        boolean result = jwtValidationService.validateJwt(jwt);
        assertEquals(true, result, "Caso 1: JWT válido com Role=Admin, Seed=7841 (primo) e Name sem números");
    }

    @Test
    public void testCaso2_JWTInvalido() {
        String jwt = "eyJhbGciOiJzI1NiJ9.dfsdfsfryJSr2xrIjoiQWRtaW4iLCJTZrkIjoiNzg0MSIsIk5hbrUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05fsdfsIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        boolean result = jwtValidationService.validateJwt(jwt);
        assertEquals(false, result, "Caso 2: JWT inválido com formato incorreto");
    }

    @Test
    public void testCaso3_NomeComNumeros() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiRXh0ZXJuYWwiLCJTZWVkIjoiODgwMzciLCJOYW1lIjoiTTRyaWEgT2xpdmlhIn0.6YD73XWZYQSSMDf6H0i3-kylz1-TY_Yt6h1cV2Ku-Qs";
        boolean result = jwtValidationService.validateJwt(jwt);
        assertEquals(false, result, "Caso 3: JWT com Name contendo números (M4ria)");
    }

    @Test
    public void testCaso4_MaisDeTresClaims() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiTWVtYmVyIiwiT3JnIjoiQlIiLCJTZWVkIjoiMTQ2MjciLCJOYW1lIjoiVmFsZGlyIEFyYW5oYSJ9.cmrXV_Flm5mfdpfNUVopY_I2zeJUy4EZ4i3Fea98zvY";
        boolean result = jwtValidationService.validateJwt(jwt);
        assertEquals(false, result, "Caso 4: JWT com mais de 3 claims (possui Org adicional)");
    }
}