package com.ahuanay.empleado.domain.model;

import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmpleadoTest {

    private static final String NOMBRE = "Anderson Bryam";
    private static final String APELLIDO = "Huanay Carhuaricra";
    private static final String EMAIL = "ahuanay@example.com";
    private static final Puesto PUESTO = Puesto.DESARROLLADOR_SENIOR;
    private static final BigDecimal SALARIO = new BigDecimal("4500.00");
    private static final LocalDate FECHA_INGRESO = LocalDate.of(2025, 10, 15);

    @Test
    @DisplayName("Debería crear un empleado exitosamente aplicando trim y validando datos")
    void deberiaCrearEmpleadoExitosamente() {
        // Act
        Empleado empleado = Empleado.crear(
            "  Anderson Bryam  ",
            "  Huanay Carhuaricra  ",
            " AHUANAY@Example.com ",
            PUESTO,
            SALARIO,
            FECHA_INGRESO
        );

        // Assert
        assertNotNull(empleado);
        assertNull(empleado.getId());
        assertEquals(NOMBRE, empleado.getNombre());
        assertEquals(APELLIDO, empleado.getApellido());
        assertEquals(EMAIL, empleado.getEmail().valor());
        assertEquals(PUESTO, empleado.getPuesto());
        assertEquals(0, SALARIO.compareTo(empleado.getSalario().valor()));
        assertEquals(FECHA_INGRESO, empleado.getFechaIngreso());
    }

    @Test
    @DisplayName("Debería normalizar el salario a dos decimales")
    void deberiaNormalizarSalarioADosDecimales() {
        // Act
        Empleado empleado = Empleado.crear(NOMBRE, APELLIDO, EMAIL, PUESTO, new BigDecimal("4500.5"), FECHA_INGRESO);

        // Assert
        assertEquals(new BigDecimal("4500.50"), empleado.getSalario().valor());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Debería lanzar excepción cuando el nombre es nulo, vacío o en blanco")
    void deberiaLanzarExcepcionCuandoNombreEsInvalido(String nombreInvalido) {
        EmpleadoDomainException exception = assertThrows(
            EmpleadoDomainException.class,
            () -> Empleado.crear(nombreInvalido, APELLIDO, EMAIL, PUESTO, SALARIO, FECHA_INGRESO)
        );

        assertEquals("El nombre del empleado no puede ser nulo o vacío.", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Debería lanzar excepción cuando el apellido es nulo, vacío o en blanco")
    void deberiaLanzarExcepcionCuandoApellidoEsInvalido(String apellidoInvalido) {
        EmpleadoDomainException exception = assertThrows(
            EmpleadoDomainException.class,
            () -> Empleado.crear(NOMBRE, apellidoInvalido, EMAIL, PUESTO, SALARIO, FECHA_INGRESO)
        );

        assertEquals("El apellido del empleado no puede ser nulo o vacío.", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n", "correo-invalido", "anderson@", "@ahuanay.com", "anderson.ahuanay.com"})
    @DisplayName("Debería lanzar excepción cuando el email es inválido, nulo o vacío")
    void deberiaLanzarExcepcionCuandoEmailEsInvalido(String emailInvalido) {
        EmpleadoDomainException exception = assertThrows(
            EmpleadoDomainException.class,
            () -> Empleado.crear(NOMBRE, APELLIDO, emailInvalido, PUESTO, SALARIO, FECHA_INGRESO)
        );

        assertTrue(exception.getMessage().contains("email"));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el puesto es nulo")
    void deberiaLanzarExcepcionCuandoPuestoEsNulo() {
        EmpleadoDomainException exception = assertThrows(
            EmpleadoDomainException.class,
            () -> Empleado.crear(NOMBRE, APELLIDO, EMAIL, null, SALARIO, FECHA_INGRESO)
        );

        assertEquals("El puesto del empleado es obligatorio y debe ser un valor válido.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "0.00", "-100.00", "-1.00"})
    @DisplayName("Debería lanzar excepción cuando el salario es nulo, cero o negativo")
    void deberiaLanzarExcepcionCuandoSalarioEsInvalido(BigDecimal salarioInvalido) {
        EmpleadoDomainException exception = assertThrows(
            EmpleadoDomainException.class,
            () -> Empleado.crear(NOMBRE, APELLIDO, EMAIL, PUESTO, salarioInvalido, FECHA_INGRESO)
        );

        assertEquals("El salario debe ser mayor a cero.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la fecha de ingreso es nula")
    void deberiaLanzarExcepcionCuandoFechaIngresoEsNula() {
        EmpleadoDomainException exception = assertThrows(
            EmpleadoDomainException.class,
            () -> Empleado.crear(NOMBRE, APELLIDO, EMAIL, PUESTO, SALARIO, null)
        );

        assertEquals("La fecha de ingreso es obligatoria.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería reconstruir un empleado persistido conservando su identificador")
    void deberiaReconstruirEmpleadoConId() {
        // Act
        Empleado empleado = Empleado.reconstruir(7L, NOMBRE, APELLIDO, EMAIL, PUESTO, SALARIO, FECHA_INGRESO);

        // Assert
        assertEquals(7L, empleado.getId());
        assertEquals(EMAIL, empleado.getEmail().valor());
    }

    @Test
    @DisplayName("Debería lanzar excepción al reconstruir un empleado sin identificador")
    void deberiaLanzarExcepcionAlReconstruirSinId() {
        EmpleadoDomainException exception = assertThrows(
            EmpleadoDomainException.class,
            () -> Empleado.reconstruir(null, NOMBRE, APELLIDO, EMAIL, PUESTO, SALARIO, FECHA_INGRESO)
        );

        assertEquals("No se puede reconstruir un empleado sin identificador.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería actualizar los datos conservando la identidad del empleado")
    void deberiaActualizarDatosConservandoId() {
        // Arrange
        Empleado original = Empleado.reconstruir(7L, NOMBRE, APELLIDO, EMAIL, PUESTO, SALARIO, FECHA_INGRESO);

        // Act
        Empleado actualizado = original.actualizarDatos(
            "Carlos Alberto",
            "Mendoza Rios",
            "carlos.mendoza@example.com",
            Puesto.TECH_LEAD,
            new BigDecimal("8000.00"),
            LocalDate.of(2026, 1, 5)
        );

        // Assert
        assertEquals(7L, actualizado.getId());
        assertEquals("Carlos Alberto", actualizado.getNombre());
        assertEquals("carlos.mendoza@example.com", actualizado.getEmail().valor());
        assertEquals(Puesto.TECH_LEAD, actualizado.getPuesto());
        assertEquals(0, new BigDecimal("8000.00").compareTo(actualizado.getSalario().valor()));
        assertEquals(LocalDate.of(2026, 1, 5), actualizado.getFechaIngreso());

        // El agregado es inmutable: el original no cambia
        assertEquals(NOMBRE, original.getNombre());
        assertEquals(PUESTO, original.getPuesto());
    }

    @Test
    @DisplayName("Debería ajustar el salario conservando el resto de los datos")
    void deberiaAjustarSalarioConservandoRestoDeDatos() {
        // Arrange
        Empleado original = Empleado.reconstruir(7L, NOMBRE, APELLIDO, EMAIL, PUESTO, SALARIO, FECHA_INGRESO);

        // Act
        Empleado actualizado = original.ajustarSalario(new BigDecimal("5500.00"));

        // Assert
        assertEquals(7L, actualizado.getId());
        assertEquals(NOMBRE, actualizado.getNombre());
        assertEquals(0, new BigDecimal("5500.00").compareTo(actualizado.getSalario().valor()));
    }
}
