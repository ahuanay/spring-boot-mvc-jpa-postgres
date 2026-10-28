package com.ahuanay.empleado.application.command.update;

import com.ahuanay.empleado.domain.enums.Puesto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateEmpleadoCommand(
    Long id,
    String nombre,
    String apellido,
    String email,
    Puesto puesto,
    BigDecimal salario,
    LocalDate fechaIngreso
) {
}
