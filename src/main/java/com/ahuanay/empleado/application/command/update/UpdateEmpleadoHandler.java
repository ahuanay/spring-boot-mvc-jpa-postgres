package com.ahuanay.empleado.application.command.update;

import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;
import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
import com.ahuanay.empleado.domain.model.Empleado;
import com.ahuanay.empleado.domain.repository.EmpleadoRepository;
import org.springframework.transaction.annotation.Transactional;

public class UpdateEmpleadoHandler {

    private final EmpleadoRepository empleadoRepository;

    public UpdateEmpleadoHandler(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public void handle(UpdateEmpleadoCommand command) {
        Empleado empleado = empleadoRepository.findById(command.id())
            .orElseThrow(() -> new EmpleadoNotFoundException(command.id()));

        Empleado empleadoActualizado = empleado.actualizarDatos(
            command.nombre(),
            command.apellido(),
            command.email(),
            command.puesto(),
            command.salario(),
            command.fechaIngreso()
        );

        String emailNormalizado = empleadoActualizado.getEmail().valor();

        if (empleadoRepository.existsByEmailAndIdNot(emailNormalizado, command.id())) {
            throw new EmpleadoDomainException("Ya existe un empleado activo con el email: " + emailNormalizado);
        }

        empleadoRepository.save(empleadoActualizado);
    }
}
