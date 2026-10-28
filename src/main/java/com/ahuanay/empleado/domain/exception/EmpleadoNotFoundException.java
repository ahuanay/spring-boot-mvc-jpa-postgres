package com.ahuanay.empleado.domain.exception;

import java.util.Arrays;
import java.util.List;

public class EmpleadoNotFoundException extends RuntimeException {

    private final List<Long> notFoundIds;

    public EmpleadoNotFoundException(Long... ids) {
        this(ids == null ? List.of() : Arrays.asList(ids));
    }

    public EmpleadoNotFoundException(List<Long> ids) {
        super("No se encontró el recurso solicitado.");
        this.notFoundIds = ids != null ? List.copyOf(ids) : List.of();
    }

    public List<Long> getNotFoundIds() {
        return notFoundIds;
    }
}