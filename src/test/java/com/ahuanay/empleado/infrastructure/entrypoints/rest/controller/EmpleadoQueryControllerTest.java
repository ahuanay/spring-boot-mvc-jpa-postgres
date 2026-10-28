package com.ahuanay.empleado.infrastructure.entrypoints.rest.controller;

import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosHandler;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosQuery;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosResult;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoHandler;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoQuery;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoResult;
import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestResponse;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.mapper.EmpleadoRestMapper;
import com.ahuanay.mvccommon.application.pagination.PagedReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoQueryController.class)
class EmpleadoQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetByIdEmpleadoHandler getByIdHandler;

    @MockitoBean
    private GetAllEmpleadosHandler getAllHandler;

    @MockitoBean
    private EmpleadoRestMapper restMapper;

    @Test
    @DisplayName("GET /api/v1/empleados/{id} - Debería retornar el detalle del empleado y status 200 con RestResponse")
    void deberiaObtenerEmpleadoPorIdExitosamente() throws Exception {
        // Arrange
        Long empleadoId = 105L;
        GetByIdEmpleadoResult detalleMock = new GetByIdEmpleadoResult(
            empleadoId,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        EmpleadoRestResponse restResponse = EmpleadoRestResponse.builder()
            .id(empleadoId)
            .nombre("Anderson Bryam")
            .apellido("Huanay Carhuaricra")
            .email("ahuanay@example.com")
            .puesto(Puesto.DESARROLLADOR_SENIOR)
            .salario(new BigDecimal("4500.00"))
            .fechaIngreso(LocalDate.of(2025, 10, 15))
            .build();

        when(getByIdHandler.handle(any(GetByIdEmpleadoQuery.class))).thenReturn(detalleMock);
        when(restMapper.toRestResponse(any(GetByIdEmpleadoResult.class))).thenReturn(restResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/empleados/{id}", empleadoId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Detalle del empleado recuperado con éxito"))
            .andExpect(jsonPath("$.data.id").value(empleadoId))
            .andExpect(jsonPath("$.data.nombre").value("Anderson Bryam"))
            .andExpect(jsonPath("$.data.apellido").value("Huanay Carhuaricra"))
            .andExpect(jsonPath("$.data.email").value("ahuanay@example.com"))
            .andExpect(jsonPath("$.data.puesto").value("DESARROLLADOR_SENIOR"));

        verify(getByIdHandler, times(1)).handle(any(GetByIdEmpleadoQuery.class));
        verify(restMapper, times(1)).toRestResponse(any(GetByIdEmpleadoResult.class));
    }

    @Test
    @DisplayName("GET /api/v1/empleados/{id} - Debería retornar 404 con RestResponse y notFoundIds cuando el ID no existe")
    void deberiaRetornar404CuandoEmpleadoNoExiste() throws Exception {
        // Arrange
        Long empleadoId = 99L;
        when(getByIdHandler.handle(any(GetByIdEmpleadoQuery.class)))
            .thenThrow(new EmpleadoNotFoundException(empleadoId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/empleados/{id}", empleadoId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("No se encontró el recurso solicitado."))
            .andExpect(jsonPath("$.notFoundIds[0]").value(99));

        verify(getByIdHandler, times(1)).handle(any(GetByIdEmpleadoQuery.class));
    }

    @Test
    @DisplayName("GET /api/v1/empleados - Debería retornar lista paginada de empleados con RestResponse y status 200")
    void deberiaListarEmpleadosPaginadosExitosamente() throws Exception {
        // Arrange
        GetAllEmpleadosResult resumenMock = new GetAllEmpleadosResult(
            105L,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR
        );

        PagedReadModel<GetAllEmpleadosResult> pagedMock = new PagedReadModel<>(
            List.of(resumenMock),
            0,
            10,
            1L,
            1,
            true
        );

        EmpleadoRestResponse restResponse = EmpleadoRestResponse.builder()
            .id(105L)
            .nombre("Anderson Bryam")
            .apellido("Huanay Carhuaricra")
            .email("ahuanay@example.com")
            .puesto(Puesto.DESARROLLADOR_SENIOR)
            .build();

        when(getAllHandler.handle(any(GetAllEmpleadosQuery.class))).thenReturn(pagedMock);
        when(restMapper.toRestResponse(any(GetAllEmpleadosResult.class))).thenReturn(restResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/empleados")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "id")
                .param("direction", "ASC")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Listado de empleados obtenido exitosamente"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(105L))
            .andExpect(jsonPath("$.data[0].nombre").value("Anderson Bryam"))
            .andExpect(jsonPath("$.pagination.totalElements").value(1))
            .andExpect(jsonPath("$.pagination.totalPages").value(1))
            .andExpect(jsonPath("$.pagination.isLast").value(true));

        verify(getAllHandler, times(1)).handle(any(GetAllEmpleadosQuery.class));
        verify(restMapper, times(1)).toRestResponse(any(GetAllEmpleadosResult.class));
    }
}