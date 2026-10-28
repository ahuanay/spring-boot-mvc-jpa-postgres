package com.ahuanay.empleado.application.command.create;

import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateEmpleadoHandlerTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private CreateEmpleadoHandler createEmpleadoHandler;

    @Test
    @DisplayName("Debería crear y guardar un empleado exitosamente cuando el email no existe")
    void deberiaCrearEmpleadoExitosamente() {
        // Arrange
        CreateEmpleadoCommand command = new CreateEmpleadoCommand(
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        when(empleadoRepository.existsByEmail(command.email())).thenReturn(false);

        Empleado empleadoGuardadoMock = mock(Empleado.class);
        when(empleadoGuardadoMock.getId()).thenReturn(1L);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoGuardadoMock);

        // Act
        Long empleadoId = createEmpleadoHandler.handle(command);

        // Assert
        assertNotNull(empleadoId);
        assertEquals(1L, empleadoId);

        verify(empleadoRepository, times(1)).existsByEmail(command.email());
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }

    @Test
    @DisplayName("Debería normalizar el email antes de comprobar la unicidad")
    void deberiaNormalizarEmailAntesDeVerificarUnicidad() {
        // Arrange
        CreateEmpleadoCommand command = new CreateEmpleadoCommand(
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "  ANDERSON.AHUANAY@Example.COM  ",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        Empleado empleadoGuardadoMock = mock(Empleado.class);
        when(empleadoGuardadoMock.getId()).thenReturn(1L);
        when(empleadoRepository.existsByEmail("anderson.ahuanay@example.com")).thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoGuardadoMock);

        // Act
        createEmpleadoHandler.handle(command);

        // Assert
        verify(empleadoRepository).existsByEmail("anderson.ahuanay@example.com");
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando ya existe un empleado registrado con el mismo email")
    void deberiaLanzarExcepcionCuandoEmailYaExiste() {
        // Arrange
        CreateEmpleadoCommand command = new CreateEmpleadoCommand(
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        when(empleadoRepository.existsByEmail(command.email())).thenReturn(true);

        // Act & Assert
        EmpleadoDomainException exception = assertThrows(
            EmpleadoDomainException.class,
            () -> createEmpleadoHandler.handle(command)
        );

        assertEquals("Ya existe un empleado activo con el email: anderson.ahuanay@example.com", exception.getMessage());

        verify(empleadoRepository, times(1)).existsByEmail(command.email());
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }
}
