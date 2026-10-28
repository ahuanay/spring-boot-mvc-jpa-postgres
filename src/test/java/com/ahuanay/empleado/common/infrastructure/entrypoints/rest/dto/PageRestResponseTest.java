package com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageRestResponseTest {

    @Test
    @DisplayName("Debería crear una instancia de PageRestResponse correctamente y recuperar sus valores mediante los getters del record")
    void deberiaCrearPageRestResponseExitosamente() {
        // Arrange
        List<String> contenido = List.of("Anderson Bryam", "Huanay Carhuaricra");
        int pageNumber = 0;
        int pageSize = 10;
        long totalElements = 2L;
        int totalPages = 1;
        boolean isLast = true;

        // Act
        PageRestResponse<String> response = new PageRestResponse<>(
            contenido,
            pageNumber,
            pageSize,
            totalElements,
            totalPages,
            isLast
        );

        // Assert
        assertNotNull(response);
        assertEquals(contenido, response.content());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());
        assertEquals(totalElements, response.totalElements());
        assertEquals(totalPages, response.totalPages());
        assertTrue(response.isLast());
    }
}