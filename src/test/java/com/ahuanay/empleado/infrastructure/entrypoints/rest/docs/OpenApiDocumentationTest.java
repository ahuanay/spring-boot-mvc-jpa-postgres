package com.ahuanay.empleado.infrastructure.entrypoints.rest.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contrato de documentación: verifica que el OpenAPI publicado use el dataset oficial
 * y que no queden ejemplos incoherentes (puestos inexistentes, correos de otras personas).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("El OpenAPI debería documentar el dataset oficial de ejemplo")
    void deberiaDocumentarDatasetOficial() throws Exception {
        // Act
        String openApi = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Assert
        assertTrue(openApi.contains("Anderson Bryam"));
        assertTrue(openApi.contains("Huanay Carhuaricra"));
        assertTrue(openApi.contains("ahuanay@email.com"));
        assertTrue(openApi.contains("DESARROLLADOR_SENIOR"));
        assertTrue(openApi.contains("2026-10-26"));
    }

    @Test
    @DisplayName("El OpenAPI no debería conservar ejemplos obsoletos ni puestos inexistentes")
    void noDeberiaConservarEjemplosObsoletos() throws Exception {
        // Act
        String openApi = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Assert
        assertFalse(openApi.contains("Carlos Alberto"));
        assertFalse(openApi.contains("carlos.ahuanay@email.com"));
        assertFalse(openApi.contains("carlos.mendoza@empresa.com"));
        assertFalse(openApi.contains("DESARROLLADOR_FULLSTACK"));
    }
}
