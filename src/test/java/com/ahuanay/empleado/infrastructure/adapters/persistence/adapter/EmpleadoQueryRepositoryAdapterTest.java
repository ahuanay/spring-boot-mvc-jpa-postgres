package com.ahuanay.empleado.infrastructure.adapters.persistence.adapter;

import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosQuery;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosResult;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoResult;
import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.infrastructure.adapters.persistence.entity.EmpleadoEntity;
import com.ahuanay.empleado.infrastructure.adapters.persistence.repository.EmpleadoJpaRepository;
import com.ahuanay.mvccommon.application.pagination.PagedReadModel;
import com.ahuanay.mvccommon.application.pagination.QueryPagination;
import com.ahuanay.mvccommon.infrastructure.entrypoints.rest.exception.ParametroInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoQueryRepositoryAdapterTest {

    @Mock
    private EmpleadoJpaRepository jpaRepository;

    @InjectMocks
    private EmpleadoQueryRepositoryAdapter empleadoReadRepositoryAdapter;

    @Test
    @DisplayName("Debería delegar la proyección de detalle en el repositorio JPA")
    void deberiaProyectarDetalle() {
        // Arrange
        Long empleadoId = 1L;
        GetByIdEmpleadoResult readModel = new GetByIdEmpleadoResult(
            empleadoId,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        when(jpaRepository.findResultById(empleadoId)).thenReturn(Optional.of(readModel));

        // Act
        Optional<GetByIdEmpleadoResult> resultado = empleadoReadRepositoryAdapter.findResultById(empleadoId);

        // Assert
        assertTrue(resultado.isPresent());
        assertSame(readModel, resultado.get());
        verify(jpaRepository, times(1)).findResultById(empleadoId);
    }

    @Test
    @DisplayName("Debería mapear el listado paginado a read models y respetar el ordenamiento")
    void deberiaBuscarConPaginacionYMapearReadModels() {
        // Arrange
        QueryPagination pageableParams = new QueryPagination(0, 10, "nombre", "DESC");
        GetAllEmpleadosQuery query = new GetAllEmpleadosQuery(
            pageableParams, null, null, null, null, null, null, null, null
        );

        EmpleadoEntity entidad = EmpleadoEntity.builder()
            .id(1L)
            .nombre("Anderson Bryam")
            .apellido("Huanay Carhuaricra")
            .email("anderson.ahuanay@example.com")
            .puesto(Puesto.DESARROLLADOR_SENIOR)
            .salario(new BigDecimal("4500.00"))
            .fechaIngreso(LocalDate.of(2025, 10, 15))
            .build();

        when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(entidad), PageRequest.of(0, 10), 1));

        // Act
        PagedReadModel<GetAllEmpleadosResult> resultado = empleadoReadRepositoryAdapter.search(query);

        // Assert
        assertEquals(1, resultado.content().size());
        assertEquals(0, resultado.pageNumber());
        assertEquals(10, resultado.pageSize());
        assertEquals(1L, resultado.totalElements());
        assertEquals(1, resultado.totalPages());
        assertTrue(resultado.isLast());

        GetAllEmpleadosResult resumen = resultado.content().getFirst();
        assertEquals(1L, resumen.id());
        assertEquals("Anderson Bryam", resumen.nombre());
        assertEquals("anderson.ahuanay@example.com", resumen.email());
        assertEquals(Puesto.DESARROLLADOR_SENIOR, resumen.puesto());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(jpaRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Sort.Order orden = pageableCaptor.getValue().getSort().getOrderFor("nombre");
        assertNotNull(orden);
        assertEquals(Sort.Direction.DESC, orden.getDirection());
    }

    @Test
    @DisplayName("Debería ordenar por fechaIngreso usando el nombre real de la propiedad JPA")
    void deberiaOrdenarPorFechaIngreso() {
        // Arrange
        QueryPagination pageableParams = new QueryPagination(0, 10, "fechaingreso", "ASC");
        GetAllEmpleadosQuery query = new GetAllEmpleadosQuery(
            pageableParams, null, null, null, null, null, null, null, null
        );

        when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        // Act
        empleadoReadRepositoryAdapter.search(query);

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(jpaRepository).findAll(any(Specification.class), pageableCaptor.capture());

        assertNotNull(pageableCaptor.getValue().getSort().getOrderFor("fechaIngreso"));
    }

    @Test
    @DisplayName("Debería solicitar una búsqueda sin paginar cuando size es -1")
    void deberiaBuscarSinPaginacionCuandoSizeEsMenosUno() {
        // Arrange
        QueryPagination pageableParams = new QueryPagination(0, -1, "id", "ASC");
        GetAllEmpleadosQuery query = new GetAllEmpleadosQuery(
            pageableParams, null, null, null, null, null, null, null, null
        );

        when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of()));

        // Act
        empleadoReadRepositoryAdapter.search(query);

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(jpaRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertTrue(pageableCaptor.getValue().isUnpaged());
    }

    @Test
    @DisplayName("Debería rechazar un campo de ordenamiento no permitido")
    void deberiaRechazarCampoDeOrdenamientoNoPermitido() {
        // Arrange
        QueryPagination pageableParams = new QueryPagination(0, 10, "campoInventado", "ASC");
        GetAllEmpleadosQuery query = new GetAllEmpleadosQuery(
            pageableParams, null, null, null, null, null, null, null, null
        );

        // Act & Assert
        assertThrows(
            ParametroInvalidoException.class,
            () -> empleadoReadRepositoryAdapter.search(query)
        );
        verify(jpaRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }
}
