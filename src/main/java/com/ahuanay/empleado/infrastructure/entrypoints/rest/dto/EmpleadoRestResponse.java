package com.ahuanay.empleado.infrastructure.entrypoints.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ahuanay.empleado.domain.enums.Puesto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "EmpleadoRestResponse", description = "DTO que representa la información de un empleado devuelta por la API.")
public class EmpleadoRestResponse {

    @Schema(description = "Identificador único del empleado", example = "105")
    private Long id;

    @Schema(description = "Nombre del empleado", example = "Anderson Bryam")
    private String nombre;

    @Schema(description = "Apellido del empleado", example = "Huanay Carhuaricra")
    private String apellido;

    @Schema(description = "Correo electrónico del empleado", example = "ahuanay@email.com")
    private String email;

    @Schema(description = "Puesto o cargo que desempeña el empleado", example = "DESARROLLADOR_SENIOR")
    private Puesto puesto;

    @Schema(description = "Salario mensual del empleado", example = "4500.00")
    private BigDecimal salario;

    @Schema(description = "Fecha de ingreso a la organización", example = "2026-10-26")
    private LocalDate fechaIngreso;
}