package com.ahuanay.empleado.infrastructure.entrypoints.rest.controller;

import com.ahuanay.empleado.application.command.create.CreateEmpleadoCommand;
import com.ahuanay.empleado.application.command.create.CreateEmpleadoHandler;
import com.ahuanay.empleado.application.command.delete.DeleteEmpleadoCommand;
import com.ahuanay.empleado.application.command.delete.DeleteEmpleadoHandler;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoCommand;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoHandler;
import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestRequest;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.mapper.EmpleadoRestMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmpleadoCommandController.class)
class EmpleadoCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateEmpleadoHandler createHandler;

    @MockitoBean
    private UpdateEmpleadoHandler updateHandler;

    @MockitoBean
    private DeleteEmpleadoHandler deleteHandler;

    @MockitoBean
    private EmpleadoRestMapper restMapper;

    @Test
    @DisplayName("POST /api/v1/empleados - Debería crear un empleado y retornar status 201 con el ID")
    void deberiaCrearEmpleadoExitosamente() throws Exception {
        // Arrange
        EmpleadoRestRequest request = new EmpleadoRestRequest(
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        CreateEmpleadoCommand command = mock(CreateEmpleadoCommand.class);

        when(restMapper.toCreateCommand(any(EmpleadoRestRequest.class))).thenReturn(command);
        when(createHandler.handle(command)).thenReturn(105L);

        // Act & Assert
        mockMvc.perform(post("/api/v1/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().string("105"));

        verify(restMapper, times(1)).toCreateCommand(any(EmpleadoRestRequest.class));
        verify(createHandler, times(1)).handle(command);
    }

    @Test
    @DisplayName("PUT /api/v1/empleados/{id} - Debería actualizar un empleado y retornar status 204")
    void deberiaActualizarEmpleadoExitosamente() throws Exception {
        // Arrange
        Long empleadoId = 105L;
        EmpleadoRestRequest request = new EmpleadoRestRequest(
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("5000.00"),
            LocalDate.of(2025, 10, 15)
        );

        UpdateEmpleadoCommand command = mock(UpdateEmpleadoCommand.class);

        when(restMapper.toUpdateCommand(eq(empleadoId), any(EmpleadoRestRequest.class))).thenReturn(command);
        doNothing().when(updateHandler).handle(command);

        // Act & Assert
        mockMvc.perform(put("/api/v1/empleados/{id}", empleadoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());

        verify(restMapper, times(1)).toUpdateCommand(eq(empleadoId), any(EmpleadoRestRequest.class));
        verify(updateHandler, times(1)).handle(command);
    }

    @Test
    @DisplayName("PUT /api/v1/empleados/{id} - Debería retornar 404 cuando el empleado a actualizar no existe")
    void deberiaRetornar404CuandoActualizaEmpleadoInexistente() throws Exception {
        // Arrange
        Long empleadoId = 99L;
        EmpleadoRestRequest request = new EmpleadoRestRequest(
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("5000.00"),
            LocalDate.of(2025, 10, 15)
        );

        UpdateEmpleadoCommand command = mock(UpdateEmpleadoCommand.class);

        when(restMapper.toUpdateCommand(eq(empleadoId), any(EmpleadoRestRequest.class))).thenReturn(command);
        doThrow(new EmpleadoNotFoundException(empleadoId)).when(updateHandler).handle(command);

        // Act & Assert
        mockMvc.perform(put("/api/v1/empleados/{id}", empleadoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("No se encontró el recurso solicitado."))
            .andExpect(jsonPath("$.notFoundIds[0]").value(99));

        verify(updateHandler, times(1)).handle(command);
    }

    @Test
    @DisplayName("POST /api/v1/empleados - Debería retornar 409 cuando la base de datos rechaza la unicidad")
    void deberiaRetornar409CuandoHayConflictoDeUnicidad() throws Exception {
        // Arrange
        EmpleadoRestRequest request = new EmpleadoRestRequest(
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        CreateEmpleadoCommand command = mock(CreateEmpleadoCommand.class);

        when(restMapper.toCreateCommand(any(EmpleadoRestRequest.class))).thenReturn(command);
        when(createHandler.handle(command))
            .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    @DisplayName("DELETE /api/v1/empleados/{id} - Debería dar de baja lógicamente a un empleado y retornar status 204")
    void deberiaEliminarEmpleadoExitosamente() throws Exception {
        // Arrange
        Long empleadoId = 105L;
        doNothing().when(deleteHandler).handle(any(DeleteEmpleadoCommand.class));

        // Act & Assert
        mockMvc.perform(delete("/api/v1/empleados/{id}", empleadoId))
            .andExpect(status().isNoContent());

        verify(deleteHandler, times(1)).handle(any(DeleteEmpleadoCommand.class));
    }
}