package com.ahuanay.empleado.application.command.update;

import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;
import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
import com.ahuanay.empleado.domain.model.Empleado;
import com.ahuanay.empleado.domain.repository.EmpleadoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEmpleadoHandlerTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private UpdateEmpleadoHandler updateEmpleadoHandler;

    @Test
    @DisplayName("Debería actualizar un empleado exitosamente cuando el ID existe y el email no está duplicado")
    void deberiaActualizarEmpleadoExitosamente() {
        // Arrange
        Long empleadoId = 1L;
        UpdateEmpleadoCommand command = new UpdateEmpleadoCommand(
            empleadoId,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("5000.00"),
            LocalDate.of(2025, 10, 15)
        );

        Empleado existente = Empleado.reconstruir(
            empleadoId,
            "Anderson",
            "Huanay",
            "anderson@example.com",
            Puesto.DESARROLLADOR_JUNIOR,
            new BigDecimal("4000.00"),
            LocalDate.of(2025, 10, 15)
        );

        when(empleadoRepository.findById(empleadoId)).thenReturn(Optional.of(existente));
        when(empleadoRepository.existsByEmailAndIdNot(command.email(), empleadoId)).thenReturn(false);

        // Act
        assertDoesNotThrow(() -> updateEmpleadoHandler.handle(command));

        // Assert
        verify(empleadoRepository, times(1)).findById(empleadoId);
        verify(empleadoRepository, times(1)).existsByEmailAndIdNot(command.email(), empleadoId);
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }

    @Test
    @DisplayName("Debería lanzar EmpleadoNotFoundException antes de validar datos cuando el ID no existe")
    void deberiaLanzarExcepcionCuandoEmpleadoNoExiste() {
        // Arrange
        Long empleadoId = 99L;
        UpdateEmpleadoCommand command = new UpdateEmpleadoCommand(
            empleadoId,
            "",
            "",
            "correo-invalido",
            null,
            null,
            null
        );

        when(empleadoRepository.findById(empleadoId)).thenReturn(Optional.empty());

        // Act & Assert
        EmpleadoNotFoundException exception = assertThrows(
            EmpleadoNotFoundException.class,
            () -> updateEmpleadoHandler.handle(command)
        );

        assertEquals("No se encontró el recurso solicitado.", exception.getMessage());
        verify(empleadoRepository, never()).existsByEmailAndIdNot(any(), any());
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    @DisplayName("Debería lanzar EmpleadoDomainException cuando el email ya pertenece a otro empleado")
    void deberiaLanzarExcepcionCuandoEmailYaEstaRegistradoEnOtroEmpleado() {
        // Arrange
        Long empleadoId = 1L;
        UpdateEmpleadoCommand command = new UpdateEmpleadoCommand(
            empleadoId,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "correo.duplicado@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("5000.00"),
            LocalDate.of(2025, 10, 15)
        );

        Empleado existente = Empleado.reconstruir(
            empleadoId,
            "Anderson",
            "Huanay",
            "anderson@example.com",
            Puesto.DESARROLLADOR_JUNIOR,
            new BigDecimal("4000.00"),
            LocalDate.of(2025, 10, 15)
        );

        when(empleadoRepository.findById(empleadoId)).thenReturn(Optional.of(existente));
        when(empleadoRepository.existsByEmailAndIdNot(command.email(), empleadoId)).thenReturn(true);

        // Act & Assert
        EmpleadoDomainException exception = assertThrows(
            EmpleadoDomainException.class,
            () -> updateEmpleadoHandler.handle(command)
        );

        assertEquals("Ya existe un empleado activo con el email: correo.duplicado@example.com", exception.getMessage());
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    @DisplayName("Debería normalizar el email antes de comprobar la unicidad")
    void deberiaNormalizarEmailAntesDeVerificarUnicidad() {
        // Arrange
        Long empleadoId = 1L;
        UpdateEmpleadoCommand command = new UpdateEmpleadoCommand(
            empleadoId,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "  ANDERSON.AHUANAY@Example.COM  ",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("5000.00"),
            LocalDate.of(2025, 10, 15)
        );

        Empleado existente = Empleado.reconstruir(
            empleadoId,
            "Anderson",
            "Huanay",
            "anderson@example.com",
            Puesto.DESARROLLADOR_JUNIOR,
            new BigDecimal("4000.00"),
            LocalDate.of(2025, 10, 15)
        );

        when(empleadoRepository.findById(empleadoId)).thenReturn(Optional.of(existente));
        when(empleadoRepository.existsByEmailAndIdNot("anderson.ahuanay@example.com", empleadoId)).thenReturn(false);

        // Act
        updateEmpleadoHandler.handle(command);

        // Assert
        verify(empleadoRepository).existsByEmailAndIdNot("anderson.ahuanay@example.com", empleadoId);
    }
}
