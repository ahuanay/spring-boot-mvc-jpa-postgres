package com.ahuanay.empleado.application.query.getall;

import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.mvccommon.application.pagination.Pagination;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetAllEmpleadosQuery(
    Pagination pageable,
    String nombre,
    String apellido,
    String email,
    Puesto puesto,
    BigDecimal salarioMinimo,
    BigDecimal salarioMaximo,
    LocalDate fechaIngresoDesde,
    LocalDate fechaIngresoHasta
) {
    public GetAllEmpleadosQuery {
        if (pageable == null) {
            pageable = new Pagination(0, 10, "id", "ASC");
        }
    }
}