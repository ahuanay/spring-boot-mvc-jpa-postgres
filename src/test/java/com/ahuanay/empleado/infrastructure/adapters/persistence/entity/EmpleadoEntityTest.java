package com.ahuanay.empleado.infrastructure.adapters.persistence.entity;

import com.ahuanay.empleado.domain.enums.Puesto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmpleadoEntityTest {

    @Test
    @DisplayName("Debería probar constructores, getters, setters y el builder de EmpleadoEntity")
    void deberiaProbarGettersSettersYBuilderEntity() {
        // Arrange & Act
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(1L);
        entity.setNombre("Anderson Bryam");
        entity.setApellido("Huanay Carhuaricra");
        entity.setEmail("anderson.ahuanay@example.com");
        entity.setPuesto(Puesto.DESARROLLADOR_SENIOR);
        entity.setSalario(new BigDecimal("4500.00"));
        entity.setFechaIngreso(LocalDate.of(2025, 10, 15));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setDeletedAt(LocalDateTime.now());

        // Assert
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Anderson Bryam", entity.getNombre());
        assertEquals("Huanay Carhuaricra", entity.getApellido());
        assertEquals("anderson.ahuanay@example.com", entity.getEmail());
        assertEquals(Puesto.DESARROLLADOR_SENIOR, entity.getPuesto());
        assertEquals(new BigDecimal("4500.00"), entity.getSalario());
        assertEquals(LocalDate.of(2025, 10, 15), entity.getFechaIngreso());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertNotNull(entity.getDeletedAt());

        // Act & Assert
        EmpleadoEntity entityBuilder = EmpleadoEntity.builder()
            .id(2L)
            .nombre("Test")
            .apellido("Test")
            .email("test@example.com")
            .puesto(Puesto.DESARROLLADOR_JUNIOR)
            .salario(new BigDecimal("3000.00"))
            .fechaIngreso(LocalDate.now())
            .build();

        assertNotNull(entityBuilder);
        assertEquals(2L, entityBuilder.getId());
    }
}