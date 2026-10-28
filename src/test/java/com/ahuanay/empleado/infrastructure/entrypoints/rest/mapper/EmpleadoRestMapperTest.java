package com.ahuanay.empleado.infrastructure.entrypoints.rest.mapper;

import com.ahuanay.empleado.application.command.create.CreateEmpleadoCommand;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoCommand;
import com.ahuanay.empleado.application.query.getall.EmpleadoSummaryReadModel;
import com.ahuanay.empleado.application.query.getbyid.EmpleadoDetailReadModel;
import com.ahuanay.empleado.common.application.pagination.PagedReadModel;
import com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto.PageRestResponse;
import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestRequest;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmpleadoRestMapperTest {

    private final EmpleadoRestMapper mapper = Mappers.getMapper(EmpleadoRestMapper.class);

    private static final LocalDate FECHA_INGRESO = LocalDate.of(2025, 10, 15);

    @Test
    @DisplayName("Debería mapear el read model de detalle a EmpleadoRestResponse")
    void deberiaMapearDetalleARestResponse() {
        // Arrange
        EmpleadoDetailReadModel readModel = new EmpleadoDetailReadModel(
            1L,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            FECHA_INGRESO
        );

        // Act
        EmpleadoRestResponse response = mapper.toRestResponse(readModel);

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Anderson Bryam", response.getNombre());
        assertEquals("Huanay Carhuaricra", response.getApellido());
        assertEquals("anderson.ahuanay@example.com", response.getEmail());
        assertEquals(Puesto.DESARROLLADOR_SENIOR, response.getPuesto());
        assertEquals(new BigDecimal("4500.00"), response.getSalario());
        assertEquals(FECHA_INGRESO, response.getFechaIngreso());
    }

    @Test
    @DisplayName("Debería mapear el resumen dejando nulos los campos no consultados")
    void deberiaMapearResumenARestResponse() {
        // Arrange
        EmpleadoSummaryReadModel readModel = new EmpleadoSummaryReadModel(
            1L,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR
        );

        // Act
        EmpleadoRestResponse response = mapper.toRestResponse(readModel);

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Anderson Bryam", response.getNombre());
        assertNull(response.getSalario());
        assertNull(response.getFechaIngreso());
    }

    @Test
    @DisplayName("Debería mapear el read model paginado a PageRestResponse")
    void deberiaMapearPaginaARestResponse() {
        // Arrange
        PagedReadModel<EmpleadoSummaryReadModel> paged = new PagedReadModel<>(
            List.of(new EmpleadoSummaryReadModel(
                1L,
                "Anderson Bryam",
                "Huanay Carhuaricra",
                "anderson.ahuanay@example.com",
                Puesto.DESARROLLADOR_SENIOR
            )),
            2,
            5,
            11L,
            3,
            false
        );

        // Act
        PageRestResponse<EmpleadoRestResponse> page = mapper.toPageRest(paged);

        // Assert
        assertEquals(1, page.content().size());
        assertEquals("Anderson Bryam", page.content().getFirst().getNombre());
        assertEquals(2, page.pageNumber());
        assertEquals(5, page.pageSize());
        assertEquals(11L, page.totalElements());
        assertEquals(3, page.totalPages());
        assertFalse(page.isLast());
    }

    @Test
    @DisplayName("Debería mapear el request a los comandos de creación y actualización")
    void deberiaMapearRequestAComandos() {
        // Arrange
        EmpleadoRestRequest request = EmpleadoRestRequest.builder()
            .nombre("Anderson Bryam")
            .apellido("Huanay Carhuaricra")
            .email("anderson.ahuanay@example.com")
            .puesto(Puesto.DESARROLLADOR_SENIOR)
            .salario(new BigDecimal("4500.00"))
            .fechaIngreso(FECHA_INGRESO)
            .build();

        // Act
        CreateEmpleadoCommand createCommand = mapper.toCreateCommand(request);
        UpdateEmpleadoCommand updateCommand = mapper.toUpdateCommand(7L, request);

        // Assert
        assertEquals("Anderson Bryam", createCommand.nombre());
        assertEquals("anderson.ahuanay@example.com", createCommand.email());
        assertEquals(7L, updateCommand.id());
        assertEquals(Puesto.DESARROLLADOR_SENIOR, updateCommand.puesto());
        assertEquals(FECHA_INGRESO, updateCommand.fechaIngreso());
    }
}
