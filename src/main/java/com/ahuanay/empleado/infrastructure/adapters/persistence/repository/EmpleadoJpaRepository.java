package com.ahuanay.empleado.infrastructure.adapters.persistence.repository;

import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoResult;
import com.ahuanay.empleado.infrastructure.adapters.persistence.entity.EmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmpleadoJpaRepository extends JpaRepository<EmpleadoEntity, Long>, JpaSpecificationExecutor<EmpleadoEntity> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
        select new com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoResult(
            e.id,
            e.nombre,
            e.apellido,
            e.email,
            e.puesto,
            e.salario,
            e.fechaIngreso
        )
        from EmpleadoEntity e
        where e.id = :id
        """)
    Optional<GetByIdEmpleadoResult> findResultById(@Param("id") Long id);
}
