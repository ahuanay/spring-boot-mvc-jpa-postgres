package com.ahuanay.empleado.infrastructure.adapters.persistence.repository;

import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoResult;
import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.infrastructure.adapters.persistence.entity.EmpleadoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EmpleadoJpaRepositoryTest {

    private static final String NOMBRE = "Anderson Bryam";
    private static final String APELLIDO = "Huanay Carhuaricra";
    private static final Puesto PUESTO = Puesto.DESARROLLADOR_SENIOR;
    private static final BigDecimal SALARIO = new BigDecimal("4500.00");
    private static final LocalDate FECHA_INGRESO = LocalDate.of(2025, 10, 15);

    @Autowired
    private EmpleadoJpaRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Debería ocultar los empleados con borrado lógico en consultas y validaciones de email")
    void deberiaOcultarEmpleadosConBorradoLogico() {
        // Arrange
        EmpleadoEntity activo = persistir("activo@example.com", null);
        EmpleadoEntity eliminado = persistir("eliminado@example.com", LocalDateTime.now());

        // Act
        testEntityManager.flush();
        testEntityManager.clear();

        // Assert
        assertTrue(repository.findById(activo.getId()).isPresent());
        assertTrue(repository.findById(eliminado.getId()).isEmpty());

        assertTrue(repository.existsByEmail("activo@example.com"));
        assertFalse(repository.existsByEmail("eliminado@example.com"));
    }

    @Test
    @DisplayName("Debería permitir reutilizar el email de un empleado eliminado lógicamente")
    void deberiaPermitirReutilizarEmailDeEmpleadoEliminado() {
        // Arrange
        persistir("reutilizable@example.com", LocalDateTime.now());
        testEntityManager.flush();
        testEntityManager.clear();

        // Act
        boolean existe = repository.existsByEmail("reutilizable@example.com");

        // Assert
        assertFalse(existe);
    }

    @Test
    @DisplayName("Debería proyectar el read model de detalle sin cargar el agregado")
    void deberiaProyectarDetalleDesdeJpql() {
        // Arrange
        EmpleadoEntity entity = persistir("detalle@example.com", null);
        testEntityManager.flush();
        testEntityManager.clear();

        // Act
        Optional<GetByIdEmpleadoResult> detalle = repository.findResultById(entity.getId());

        // Assert
        assertTrue(detalle.isPresent());
        GetByIdEmpleadoResult readModel = detalle.get();
        assertEquals(entity.getId(), readModel.id());
        assertEquals(NOMBRE, readModel.nombre());
        assertEquals(APELLIDO, readModel.apellido());
        assertEquals("detalle@example.com", readModel.email());
        assertEquals(PUESTO, readModel.puesto());
        assertEquals(0, SALARIO.compareTo(readModel.salario()));
        assertEquals(FECHA_INGRESO, readModel.fechaIngreso());
    }

    @Test
    @DisplayName("Debería ocultar el detalle de un empleado eliminado lógicamente")
    void deberiaOcultarDetalleDeEmpleadoEliminado() {
        // Arrange
        EmpleadoEntity eliminado = persistir("borrado@example.com", LocalDateTime.now());
        testEntityManager.flush();
        testEntityManager.clear();

        // Act
        Optional<GetByIdEmpleadoResult> detalle = repository.findResultById(eliminado.getId());

        // Assert
        assertTrue(detalle.isEmpty());
    }

    private EmpleadoEntity persistir(String email, LocalDateTime deletedAt) {
        EmpleadoEntity entity = EmpleadoEntity.builder()
            .nombre(NOMBRE)
            .apellido(APELLIDO)
            .email(email)
            .puesto(PUESTO)
            .salario(SALARIO)
            .fechaIngreso(FECHA_INGRESO)
            .deletedAt(deletedAt)
            .build();

        testEntityManager.persist(entity);
        testEntityManager.flush();
        return entity;
    }
}