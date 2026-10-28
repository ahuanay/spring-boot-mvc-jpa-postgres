package com.ahuanay.empleado.domain.repository;

import com.ahuanay.empleado.domain.model.Empleado;

import java.util.Optional;

public interface EmpleadoRepository {

    Empleado save(Empleado empleado);

    Optional<Empleado> findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
