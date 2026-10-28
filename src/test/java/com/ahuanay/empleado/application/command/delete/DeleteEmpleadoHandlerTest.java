package com.ahuanay.empleado.application.command.delete;

import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
import com.ahuanay.empleado.domain.model.Empleado;
import com.ahuanay.empleado.domain.repository.EmpleadoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteEmpleadoHandlerTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private DeleteEmpleadoHandler deleteEmpleadoHandler;

    @Test
    @DisplayName("Debería eliminar un empleado exitosamente cuando el ID existe")
    void deberiaEliminarEmpleadoExitosamente() {
        // Arrange
        Long empleadoId = 1L;
        DeleteEmpleadoCommand command = new DeleteEmpleadoCommand(empleadoId);

        Empleado empleadoMock = mock(Empleado.class);
        when(empleadoRepository.findById(empleadoId)).thenReturn(Optional.of(empleadoMock));
        doNothing().when(empleadoRepository).deleteById(empleadoId);

        // Act
        assertDoesNotThrow(() -> deleteEmpleadoHandler.handle(command));

        // Assert
        verify(empleadoRepository, times(1)).findById(empleadoId);
        verify(empleadoRepository, times(1)).deleteById(empleadoId);
    }

    @Test
    @DisplayName("Debería lanzar EmpleadoNotFoundException cuando el ID no existe al intentar eliminar")
    void deberiaLanzarExcepcionCuandoEmpleadoNoExiste() {
        // Arrange
        Long empleadoId = 99L;
        DeleteEmpleadoCommand command = new DeleteEmpleadoCommand(empleadoId);

        when(empleadoRepository.findById(empleadoId)).thenReturn(Optional.empty());

        // Act & Assert
        EmpleadoNotFoundException exception = assertThrows(
            EmpleadoNotFoundException.class,
            () -> deleteEmpleadoHandler.handle(command)
        );

        assertEquals("No se encontró el recurso solicitado.", exception.getMessage());

        verify(empleadoRepository, times(1)).findById(empleadoId);
        verify(empleadoRepository, never()).deleteById(anyLong());
    }
}