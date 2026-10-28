package com.ahuanay.empleado.infrastructure.adapters.persistence.mapper;

import com.ahuanay.empleado.domain.model.Empleado;
import com.ahuanay.empleado.infrastructure.adapters.persistence.entity.EmpleadoEntity;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper {

    public Empleado toDomain(EmpleadoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Empleado.reconstruir(
            entity.getId(),
            entity.getNombre(),
            entity.getApellido(),
            entity.getEmail(),
            entity.getPuesto(),
            entity.getSalario(),
            entity.getFechaIngreso()
        );
    }

    public EmpleadoEntity toEntity(Empleado empleado) {
        if (empleado == null) {
            return null;
        }
        return EmpleadoEntity.builder()
            .id(empleado.getId())
            .nombre(empleado.getNombre())
            .apellido(empleado.getApellido())
            .email(empleado.getEmail().valor())
            .puesto(empleado.getPuesto())
            .salario(empleado.getSalario().valor())
            .fechaIngreso(empleado.getFechaIngreso())
            .build();
    }
}
