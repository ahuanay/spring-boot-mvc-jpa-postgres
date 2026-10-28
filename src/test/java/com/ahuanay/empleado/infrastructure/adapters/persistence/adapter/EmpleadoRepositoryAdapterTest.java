package com.ahuanay.empleado.infrastructure.adapters.persistence.adapter;

import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.domain.model.Empleado;
import com.ahuanay.empleado.infrastructure.adapters.persistence.entity.EmpleadoEntity;
import com.ahuanay.empleado.infrastructure.adapters.persistence.mapper.EmpleadoMapper;
import com.ahuanay.empleado.infrastructure.adapters.persistence.repository.EmpleadoJpaRepository;
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
class EmpleadoRepositoryAdapterTest {

    @Mock
    private EmpleadoJpaRepository jpaRepository;

    @Mock
    private EmpleadoMapper mapper;

    @InjectMocks
    private EmpleadoRepositoryAdapter empleadoRepositoryAdapter;

    @Test
    @DisplayName("Debería guardar y retornar un empleado exitosamente mapeando entidad y dominio")
    void deberiaGuardarEmpleadoExitosamente() {
        // Arrange
        Empleado empleadoDominio = Empleado.crear(
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        EmpleadoEntity entidadMock = new EmpleadoEntity();
        EmpleadoEntity entidadGuardadaMock = new EmpleadoEntity();
        entidadGuardadaMock.setId(1L);

        Empleado empleadoGuardado = Empleado.reconstruir(
            1L,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        when(mapper.toEntity(empleadoDominio)).thenReturn(entidadMock);
        when(jpaRepository.save(entidadMock)).thenReturn(entidadGuardadaMock);
        when(mapper.toDomain(entidadGuardadaMock)).thenReturn(empleadoGuardado);

        // Act
        Empleado resultado = empleadoRepositoryAdapter.save(empleadoDominio);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Anderson Bryam", resultado.getNombre());
        assertEquals("anderson.ahuanay@example.com", resultado.getEmail().valor());

        verify(mapper, times(1)).toEntity(empleadoDominio);
        verify(jpaRepository, times(1)).save(entidadMock);
        verify(mapper, times(1)).toDomain(entidadGuardadaMock);
    }

    @Test
    @DisplayName("Debería encontrar un empleado por ID exitosamente")
    void deberiaEncontrarEmpleadoPorIdExitosamente() {
        // Arrange
        Long empleadoId = 1L;
        EmpleadoEntity entidadMock = new EmpleadoEntity();
        entidadMock.setId(empleadoId);

        Empleado empleadoDominio = Empleado.reconstruir(
            empleadoId,
            "Anderson Bryam",
            "Huanay Carhuaricra",
            "anderson.ahuanay@example.com",
            Puesto.DESARROLLADOR_SENIOR,
            new BigDecimal("4500.00"),
            LocalDate.of(2025, 10, 15)
        );

        when(jpaRepository.findById(empleadoId)).thenReturn(Optional.of(entidadMock));
        when(mapper.toDomain(entidadMock)).thenReturn(empleadoDominio);

        // Act
        Optional<Empleado> resultado = empleadoRepositoryAdapter.findById(empleadoId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(empleadoId, resultado.get().getId());
        assertEquals("Anderson Bryam", resultado.get().getNombre());

        verify(jpaRepository, times(1)).findById(empleadoId);
        verify(mapper, times(1)).toDomain(entidadMock);
    }

    @Test
    @DisplayName("Debería retornar Optional vacío cuando el ID no existe en la búsqueda")
    void deberiaRetornarVacioCuandoIdNoExiste() {
        // Arrange
        Long empleadoId = 99L;
        when(jpaRepository.findById(empleadoId)).thenReturn(Optional.empty());

        // Act
        Optional<Empleado> resultado = empleadoRepositoryAdapter.findById(empleadoId);

        // Assert
        assertFalse(resultado.isPresent());
        verify(jpaRepository, times(1)).findById(empleadoId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Debería eliminar un empleado lógicamente (soft delete) actualizando deletedAt y deletedBy")
    void deberiaEliminarEmpleadoLogicamente() {
        // Arrange
        Long empleadoId = 1L;
        EmpleadoEntity entidadMock = new EmpleadoEntity();
        entidadMock.setId(empleadoId);

        when(jpaRepository.findById(empleadoId)).thenReturn(Optional.of(entidadMock));
        when(jpaRepository.save(any(EmpleadoEntity.class))).thenReturn(entidadMock);

        // Act
        assertDoesNotThrow(() -> empleadoRepositoryAdapter.deleteById(empleadoId));

        // Assert
        assertNotNull(entidadMock.getDeletedAt());
        assertEquals("SYSTEM", entidadMock.getDeletedBy());
        verify(jpaRepository, times(1)).findById(empleadoId);
        verify(jpaRepository, times(1)).save(entidadMock);
    }

    @Test
    @DisplayName("No debería fallar al eliminar un empleado inexistente")
    void deberiaIgnorarEliminacionDeEmpleadoInexistente() {
        // Arrange
        Long empleadoId = 99L;
        when(jpaRepository.findById(empleadoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> empleadoRepositoryAdapter.deleteById(empleadoId));
        verify(jpaRepository, never()).save(any(EmpleadoEntity.class));
    }

    @Test
    @DisplayName("Debería verificar existencia por ID exitosamente")
    void deberiaVerificarExistenciaPorId() {
        // Arrange
        Long empleadoId = 1L;
        when(jpaRepository.existsById(empleadoId)).thenReturn(true);

        // Act
        boolean existe = empleadoRepositoryAdapter.existsById(empleadoId);

        // Assert
        assertTrue(existe);
        verify(jpaRepository, times(1)).existsById(empleadoId);
    }

    @Test
    @DisplayName("Debería verificar existencia por email exitosamente")
    void deberiaVerificarExistenciaPorEmail() {
        // Arrange
        String email = "anderson.ahuanay@example.com";
        when(jpaRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean existe = empleadoRepositoryAdapter.existsByEmail(email);

        // Assert
        assertTrue(existe);
        verify(jpaRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Debería verificar existencia por email excluyendo un ID exitosamente")
    void deberiaVerificarExistenciaPorEmailExcluyendoId() {
        // Arrange
        String email = "anderson.ahuanay@example.com";
        Long empleadoId = 1L;
        when(jpaRepository.existsByEmailAndIdNot(email, empleadoId)).thenReturn(true);

        // Act
        boolean existe = empleadoRepositoryAdapter.existsByEmailAndIdNot(email, empleadoId);

        // Assert
        assertTrue(existe);
        verify(jpaRepository, times(1)).existsByEmailAndIdNot(email, empleadoId);
    }
}
