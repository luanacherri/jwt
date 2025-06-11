package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testJwtValidoDeveRetornarTrue() throws Exception {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        
        // Teste via POST
        mockMvc.perform(post("/api/jwt/validate")
                .contentType("text/plain")
                .content(jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Teste via GET
        mockMvc.perform(get("/api/jwt/validate")
                .param("jwt", jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testJwtInvalidoDeveRetornarFalse() throws Exception {
        String jwt = "eyJhbGciOiJzI1NiJ9.dfsdfsfryJSr2xrIjoiQWRtaW4iLCJTZrkIjoiNzg0MSIsIk5hbrUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05fsdfsIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
        
        mockMvc.perform(post("/api/jwt/validate")
                .contentType("text/plain")
                .content(jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void testNomeComNumerosDeveRetornarFalse() throws Exception {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiRXh0ZXJuYWwiLCJTZWVkIjoiODgwMzciLCJOYW1lIjoiTTRyaWEgT2xpdmlhIn0.6YD73XWZYQSSMDf6H0i3-kylz1-TY_Yt6h1cV2Ku-Qs";
        
        mockMvc.perform(post("/api/jwt/validate")
                .contentType("text/plain")
                .content(jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void testMaisDeTresClaimsDeveRetornarFalse() throws Exception {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiTWVtYmVyIiwiT3JnIjoiQlIiLCJTZWVkIjoiMTQ2MjciLCJOYW1lIjoiVmFsZGlyIEFyYW5oYSJ9.cmrXV_Flm5mfdpfNUVopY_I2zeJUy4EZ4i3Fea98zvY";
        
        mockMvc.perform(post("/api/jwt/validate")
                .contentType("text/plain")
                .content(jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
