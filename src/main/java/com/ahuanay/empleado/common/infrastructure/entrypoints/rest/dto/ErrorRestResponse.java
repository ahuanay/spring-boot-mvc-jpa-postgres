package com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Schema(name = "ErrorRestResponse", description = "Estructura estándar para las respuestas de error devueltas por la API.")
public record ErrorRestResponse(

    @Schema(description = "Fecha y hora en que ocurrió el error", example = "2026-10-26T18:45:30.123")
    LocalDateTime timestamp,

    @Schema(description = "Código de estado HTTP", example = "404")
    int status,

    @Schema(description = "Tipo de error HTTP", example = "Not Found")
    String error,

    @Schema(description = "Mensaje descriptivo del error", example = "No se encontró el empleado con el ID: 99")
    String message,

    @Schema(description = "Mapa de errores específicos por campo (presente solo en fallos de validación)", example = "{\"email\": \"Formato de email inválido\"}")
    Map<String, String> errors,

    @Schema(description = "Lista de identificadores que no fueron encontrados en el sistema", example = "[99]")
    List<Long> notFoundIds
) {
    public ErrorRestResponse {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}