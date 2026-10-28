package com.ahuanay.empleado.application.query.getall;

import com.ahuanay.empleado.application.query.EmpleadoQueryRepository;
import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.mvccommon.application.pagination.PagedReadModel;
import com.ahuanay.mvccommon.application.pagination.QueryPagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllEmpleadosHandlerTest {

    @Mock
    private EmpleadoQueryRepository empleadoQueryRepository;

    @InjectMocks
    private GetAllEmpleadosHandler getAllEmpleadosHandler;

    @Test
    @DisplayName("Debería delegar la búsqueda paginada al puerto de lectura")
    void deberiaRetornarEmpleadosPaginadosExitosamente() {
        // Arrange
        QueryPagination pageable = new QueryPagination(0, 10, "nombre", "ASC");
        GetAllEmpleadosQuery query = new GetAllEmpleadosQuery(pageable, null, null, null, null, null, null, null, null);

        GetAllEmpleadosResult resumen = new GetAllEmpleadosResult(
            1L,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR
        );

        PagedReadModel<GetAllEmpleadosResult> pagedMock = new PagedReadModel<>(
            List.of(resumen),
            0,
            10,
            1L,
            1,
            true
        );

        when(empleadoQueryRepository.search(query)).thenReturn(pagedMock);

        // Act
        PagedReadModel<GetAllEmpleadosResult> resultado = getAllEmpleadosHandler.handle(query);

        // Assert
        assertSame(pagedMock, resultado);
        assertEquals(1, resultado.content().size());
        assertEquals("Anderson Bryam", resultado.content().getFirst().nombre());
        verify(empleadoQueryRepository, times(1)).search(query);
    }

    @Test
    @DisplayName("Debería retornar una estructura paginada vacía cuando no existen empleados registrados")
    void deberiaRetornarPaginaVaciaCuandoNoHayEmpleados() {
        // Arrange
        QueryPagination pageable = new QueryPagination(0, 10, "nombre", "ASC");
        GetAllEmpleadosQuery query = new GetAllEmpleadosQuery(pageable, null, null, null, null, null, null, null, null);

        PagedReadModel<GetAllEmpleadosResult> pagedVacio = new PagedReadModel<>(List.of(), 0, 10, 0L, 0, true);

        when(empleadoQueryRepository.search(query)).thenReturn(pagedVacio);

        // Act
        PagedReadModel<GetAllEmpleadosResult> resultado = getAllEmpleadosHandler.handle(query);

        // Assert
        assertTrue(resultado.content().isEmpty());
        assertEquals(0, resultado.totalElements());
        verify(empleadoQueryRepository, times(1)).search(query);
    }
}
