package com.ahuanay.empleado.application.query.getbyid;

import com.ahuanay.empleado.application.query.EmpleadoQueryRepository;
import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetByIdEmpleadoHandlerTest {

    @Mock
    private EmpleadoQueryRepository empleadoQueryRepository;

    @InjectMocks
    private GetByIdEmpleadoHandler getByIdEmpleadoHandler;

    @Test
    @DisplayName("Debería retornar el read model cuando el ID existe")
    void deberiaRetornarDetalleEmpleadoExitosamente() {
        // Arrange
        Long empleadoId = 1L;
        GetByIdEmpleadoQuery query = new GetByIdEmpleadoQuery(empleadoId);

        EmpleadoDetailReadModel readModel = new EmpleadoDetailReadModel(
            empleadoId,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        when(empleadoQueryRepository.findDetailById(empleadoId)).thenReturn(Optional.of(readModel));

        // Act
        EmpleadoDetailReadModel resultado = getByIdEmpleadoHandler.handle(query);

        // Assert
        assertSame(readModel, resultado);
        verify(empleadoQueryRepository, times(1)).findDetailById(empleadoId);
    }

    @Test
    @DisplayName("Debería lanzar EmpleadoNotFoundException cuando el ID consultado no existe")
    void deberiaLanzarExcepcionCuandoEmpleadoNoExiste() {
        // Arrange
        Long empleadoId = 99L;
        GetByIdEmpleadoQuery query = new GetByIdEmpleadoQuery(empleadoId);

        when(empleadoQueryRepository.findDetailById(empleadoId)).thenReturn(Optional.empty());

        // Act & Assert
        EmpleadoNotFoundException exception = assertThrows(
            EmpleadoNotFoundException.class,
            () -> getByIdEmpleadoHandler.handle(query)
        );

        assertEquals("No se encontró el recurso solicitado.", exception.getMessage());
        assertEquals(99L, exception.getNotFoundIds().getFirst());
    }
}
