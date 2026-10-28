package com.ahuanay.empleado.application.query.getall;

import com.ahuanay.empleado.domain.enums.Puesto;

public record EmpleadoSummaryReadModel(
    Long id,
    String nombre,
    String apellido,
    String email,
    Puesto puesto
) {
}