package com.ahuanay.empleado.domain.model;

import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Salario(BigDecimal valor) {

    private static final int ESCALA = 2;

    public Salario {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EmpleadoDomainException("El salario debe ser mayor a cero.");
        }
        valor = valor.setScale(ESCALA, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return valor.toPlainString();
    }
}
