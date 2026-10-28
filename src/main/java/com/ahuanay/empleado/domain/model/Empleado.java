package com.ahuanay.empleado.domain.model;

import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Empleado {

    private final Long id;
    private final String nombre;
    private final String apellido;
    private final Email email;
    private final Puesto puesto;
    private final Salario salario;
    private final LocalDate fechaIngreso;

    private Empleado(
        Long id,
        String nombre,
        String apellido,
        Email email,
        Puesto puesto,
        Salario salario,
        LocalDate fechaIngreso
    ) {
        this.id = id;
        this.nombre = validarTexto(nombre, "El nombre del empleado no puede ser nulo o vacío.");
        this.apellido = validarTexto(apellido, "El apellido del empleado no puede ser nulo o vacío.");
        this.email = email;
        this.puesto = validarPuesto(puesto);
        this.salario = salario;
        this.fechaIngreso = validarFechaIngreso(fechaIngreso);
    }

    public static Empleado crear(
        String nombre,
        String apellido,
        String email,
        Puesto puesto,
        BigDecimal salario,
        LocalDate fechaIngreso
    ) {
        return new Empleado(null, nombre, apellido, new Email(email), puesto, new Salario(salario), fechaIngreso);
    }

    public static Empleado reconstruir(
        Long id,
        String nombre,
        String apellido,
        String email,
        Puesto puesto,
        BigDecimal salario,
        LocalDate fechaIngreso
    ) {
        if (id == null) {
            throw new EmpleadoDomainException("No se puede reconstruir un empleado sin identificador.");
        }
        return new Empleado(id, nombre, apellido, new Email(email), puesto, new Salario(salario), fechaIngreso);
    }

    private static String validarTexto(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new EmpleadoDomainException(mensaje);
        }
        return valor.trim();
    }

    private static Puesto validarPuesto(Puesto puesto) {
        if (puesto == null) {
            throw new EmpleadoDomainException("El puesto del empleado es obligatorio y debe ser un valor válido.");
        }
        return puesto;
    }

    private static LocalDate validarFechaIngreso(LocalDate fechaIngreso) {
        if (fechaIngreso == null) {
            throw new EmpleadoDomainException("La fecha de ingreso es obligatoria.");
        }
        return fechaIngreso;
    }

    public Empleado actualizarDatos(
        String nombre,
        String apellido,
        String email,
        Puesto puesto,
        BigDecimal salario,
        LocalDate fechaIngreso
    ) {
        return new Empleado(this.id, nombre, apellido, new Email(email), puesto, new Salario(salario), fechaIngreso);
    }

    public Empleado ajustarSalario(BigDecimal nuevoSalario) {
        return new Empleado(
            this.id,
            this.nombre,
            this.apellido,
            this.email,
            this.puesto,
            new Salario(nuevoSalario),
            this.fechaIngreso
        );
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Email getEmail() {
        return email;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public Salario getSalario() {
        return salario;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }
}
