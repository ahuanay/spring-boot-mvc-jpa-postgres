package com.ahuanay.empleado.application.command.delete;

import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
import com.ahuanay.empleado.domain.repository.EmpleadoRepository;
import org.springframework.transaction.annotation.Transactional;

public class DeleteEmpleadoHandler {

    private final EmpleadoRepository empleadoRepository;

    public DeleteEmpleadoHandler(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public void handle(DeleteEmpleadoCommand command) {
        empleadoRepository.findById(command.id())
            .orElseThrow(() -> new EmpleadoNotFoundException(command.id()));

        empleadoRepository.deleteById(command.id());
    }
}
