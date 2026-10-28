package com.ahuanay.empleado.application.command.create;

import com.ahuanay.empleado.domain.enums.Puesto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEmpleadoCommand(
    String nombre,
    String apellido,
    String email,
    Puesto puesto,
    BigDecimal salario,
    LocalDate fechaIngreso
) {
}
