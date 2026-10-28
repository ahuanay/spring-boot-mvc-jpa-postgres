package com.ahuanay.empleado.application.query.getall;

import com.ahuanay.empleado.common.application.pagination.QueryPagination;
import com.ahuanay.empleado.domain.enums.Puesto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetAllEmpleadosQuery(
    QueryPagination pageable,
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
            pageable = new QueryPagination(0, 10, "id", "ASC");
        }
    }
}