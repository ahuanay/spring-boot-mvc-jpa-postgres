package com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorRestResponseTest {

    @Test
    @DisplayName("Debería crear una instancia de ErrorRestResponse correctamente usando el Builder y los getters del record")
    void deberiaCrearErrorRestResponseExitosamente() {
        // Arrange
        LocalDateTime now = LocalDateTime.of(2026, 10, 27, 12, 0, 0);
        int status = 400;
        String error = "Validation Failed";
        String message = "La validación ha fallado.";
        Map<String, String> errorsMap = Map.of("nombre", "El nombre es obligatorio");
        List<Long> notFoundIdsList = List.of(99L);

        // Act
        ErrorRestResponse response = ErrorRestResponse.builder()
            .timestamp(now)
            .status(status)
            .error(error)
            .message(message)
            .errors(errorsMap)
            .notFoundIds(notFoundIdsList)
            .build();

        // Assert
        assertNotNull(response);
        assertEquals(now, response.timestamp());
        assertEquals(status, response.status());
        assertEquals(error, response.error());
        assertEquals(message, response.message());
        assertEquals(errorsMap, response.errors());
        assertEquals(notFoundIdsList, response.notFoundIds());
    }

    @Test
    @DisplayName("Debería asignar automáticamente el timestamp actual cuando se omite o se pasa como nulo")
    void deberiaAsignarTimestampAutomaticamenteCuandoEsNulo() {
        // Arrange & Act
        ErrorRestResponse response = ErrorRestResponse.builder()
            .timestamp(null)
            .status(500)
            .error("Internal Server Error")
            .message("Error inesperado")
            .errors(null)
            .notFoundIds(null)
            .build();

        // Assert
        assertNotNull(response);
        assertNotNull(response.timestamp());
        
        assertTrue(response.timestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}