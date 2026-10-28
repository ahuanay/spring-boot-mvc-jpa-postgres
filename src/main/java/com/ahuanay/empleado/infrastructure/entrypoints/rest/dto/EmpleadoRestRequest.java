package com.ahuanay.empleado.infrastructure.entrypoints.rest.dto;

import com.ahuanay.empleado.domain.enums.Puesto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "EmpleadoRestRequest", description = "DTO para la solicitud de creación o actualización de un empleado.")
public class EmpleadoRestRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del empleado", example = "Anderson Bryam", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Schema(description = "Apellido del empleado", example = "Huanay Carhuaricra", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Schema(description = "Correo electrónico único del empleado", example = "ahuanay@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotNull(message = "El puesto es obligatorio y debe ser un valor válido")
    @Schema(
        description = "Puesto o cargo que desempeña el empleado",
        example = "DESARROLLADOR_SENIOR",
        requiredMode = Schema.RequiredMode.REQUIRED,
        implementation = Puesto.class
    )
    private Puesto puesto;

    @NotNull(message = "El salario es obligatorio")
    @PositiveOrZero(message = "El salario no puede ser menor a cero")
    @Schema(description = "Salario mensual (debe ser mayor o igual a 0)", example = "4500", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal salario;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @Schema(description = "Fecha de ingreso a la organización (Formato: YYYY-MM-DD)", example = "2026-10-26", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate fechaIngreso;
}