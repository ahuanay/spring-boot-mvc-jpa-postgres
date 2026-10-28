package com.ahuanay.empleado.common.infrastructure.adapters.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuditableEntityTest {

    @Test
    @DisplayName("Debería inicializar createdAt y asignar SYSTEM en prePersist cuando createdBy es nulo o en blanco")
    void deberiaEjecutarPrePersistConExito() {
        // Arrange
        DummyAuditableEntity entity = new DummyAuditableEntity();
        entity.setCreatedBy(null);

        // Act
        entity.prePersist();

        // Assert
        assertNotNull(entity.getCreatedAt());
        assertEquals("SYSTEM", entity.getCreatedBy());
    }

    @Test
    @DisplayName("Debería respetar el createdBy existente y no sobrescribirlo en prePersist")
    void deberiaRespetarCreatedByExistenteEnPrePersist() {
        // Arrange
        DummyAuditableEntity entity = new DummyAuditableEntity();
        entity.setCreatedBy("ahuanay");

        // Act
        entity.prePersist();

        // Assert
        assertNotNull(entity.getCreatedAt());
        assertEquals("ahuanay", entity.getCreatedBy());
    }

    @Test
    @DisplayName("Debería actualizar updatedAt y asignar SYSTEM en preUpdate cuando updatedBy es nulo o en blanco")
    void deberiaEjecutarPreUpdateConExito() {
        // Arrange
        DummyAuditableEntity entity = new DummyAuditableEntity();
        entity.setUpdatedBy("   ");

        // Act
        entity.preUpdate();

        // Assert
        assertNotNull(entity.getUpdatedAt());
        assertEquals("SYSTEM", entity.getUpdatedBy());
    }

    @Test
    @DisplayName("Debería respetar el updatedBy existente y no sobrescribirlo en preUpdate")
    void deberiaRespetarUpdatedByExistenteEnPreUpdate() {
        // Arrange
        DummyAuditableEntity entity = new DummyAuditableEntity();
        entity.setUpdatedBy("ahuanay");

        // Act
        entity.preUpdate();

        // Assert
        assertNotNull(entity.getUpdatedAt());
        assertEquals("ahuanay", entity.getUpdatedBy());
    }

    @Test
    @DisplayName("Debería probar getters, setters y campos restantes de auditoría")
    void deberiaProbarGettersYSettersGenerales() {
        // Arrange
        DummyAuditableEntity entity = new DummyAuditableEntity();
        LocalDateTime ahora = LocalDateTime.now();

        // Act
        entity.setDeletedAt(ahora);
        entity.setDeletedBy("SYSTEM");

        // Assert
        assertEquals(ahora, entity.getDeletedAt());
        assertEquals("SYSTEM", entity.getDeletedBy());
    }

    private static class DummyAuditableEntity extends AuditableEntity {
    }
}