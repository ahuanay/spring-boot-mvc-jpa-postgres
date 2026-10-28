package com.ahuanay.empleado.application.command.create;

import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;
import com.ahuanay.empleado.domain.model.Empleado;
import com.ahuanay.empleado.domain.repository.EmpleadoRepository;
import org.springframework.transaction.annotation.Transactional;

public class CreateEmpleadoHandler {

    private final EmpleadoRepository empleadoRepository;

    public CreateEmpleadoHandler(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public Long handle(CreateEmpleadoCommand command) {
        Empleado empleado = Empleado.crear(
            command.nombre(),
            command.apellido(),
            command.email(),
            command.puesto(),
            command.salario(),
            command.fechaIngreso()
        );

        String emailNormalizado = empleado.getEmail().valor();

        if (empleadoRepository.existsByEmail(emailNormalizado)) {
            throw new EmpleadoDomainException("Ya existe un empleado activo con el email: " + emailNormalizado);
        }

        Empleado empleadoGuardado = empleadoRepository.save(empleado);

        return empleadoGuardado.getId();
    }
}
