package com.ahuanay.empleado.domain.model;

import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String valor) {

    private static final Pattern PATRON = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new EmpleadoDomainException("El email del empleado no puede ser nulo o vacío.");
        }

        String normalizado = valor.trim().toLowerCase(Locale.ROOT);

        if (!PATRON.matcher(normalizado).matches()) {
            throw new EmpleadoDomainException("El formato del email es inválido.");
        }

        valor = normalizado;
    }

    @Override
    public String toString() {
        return valor;
    }
}
