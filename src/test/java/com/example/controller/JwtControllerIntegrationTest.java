package com.example.controller;

import com.example.controller.dto.JwtValidationRequest;
import com.example.controller.dto.JwtValidationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testJwtValidoDeveRetornarSucesso() throws Exception {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        JwtValidationRequest request = new JwtValidationRequest(jwt);
        
        mockMvc.perform(post("/api/v1/jwt/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.type").value("https://api.validator.com/jwt/valid"))
                .andExpect(jsonPath("$.title").value("JWT Válido"));
    }

    @Test
    public void testJwtInvalidoDeveRetornarErro() throws Exception {
        String jwt = "eyJhbGciOiJzI1NiJ9.dfsdfsfryJSr2xrIjoiQWRtaW4iLCJTZrkIjoiNzg0MSIsIk5hbrUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05fsdfsIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        JwtValidationRequest request = new JwtValidationRequest(jwt);
        
        mockMvc.perform(post("/api/v1/jwt/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.type").value("https://api.validator.com/problems/invalid-jwt"))
                .andExpect(jsonPath("$.title").value("JWT Inválido"));
    }

    @Test
    public void testNomeComNumerosDeveRetornarErro() throws Exception {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiRXh0ZXJuYWwiLCJTZWVkIjoiODgwMzciLCJOYW1lIjoiTTRyaWEgT2xpdmlhIn0.6YD73XWZYQSSMDf6H0i3-kylz1-TY_Yt6h1cV2Ku-Qs";
        JwtValidationRequest request = new JwtValidationRequest(jwt);
        
        mockMvc.perform(post("/api/v1/jwt/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.type").value("https://api.validator.com/problems/invalid-jwt"))
                .andExpect(jsonPath("$.title").value("JWT Inválido"))
                .andExpect(jsonPath("$.detail").value("Claim 'Name' é inválido"));
    }

    @Test
    public void testMaisDeTresClaimsDeveRetornarErro() throws Exception {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiTWVtYmVyIiwiT3JnIjoiQlIiLCJTZWVkIjoiMTQ2MjciLCJOYW1lIjoiVmFsZGlyIEFyYW5oYSJ9.cmrXV_Flm5mfdpfNUVopY_I2zeJUy4EZ4i3Fea98zvY";
        JwtValidationRequest request = new JwtValidationRequest(jwt);
        
        mockMvc.perform(post("/api/v1/jwt/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.type").value("https://api.validator.com/problems/invalid-jwt"))
                .andExpect(jsonPath("$.title").value("JWT Inválido"))
                .andExpect(jsonPath("$.detail").value("JWT deve conter exatamente 3 claims (Name, Role, Seed)"));
    }

    @Test
    public void testRequestSemJwtDeveRetornarErro() throws Exception {
        mockMvc.perform(post("/api/v1/jwt/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
