package com.ahuanay.empleado.application.query.getbyid;

import com.ahuanay.empleado.application.query.EmpleadoQueryRepository;
import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;

public class GetByIdEmpleadoHandler {

    private final EmpleadoQueryRepository empleadoQueryRepository;

    public GetByIdEmpleadoHandler(EmpleadoQueryRepository empleadoQueryRepository) {
        this.empleadoQueryRepository = empleadoQueryRepository;
    }

    public EmpleadoDetailReadModel handle(GetByIdEmpleadoQuery query) {
        return empleadoQueryRepository.findDetailById(query.id())
            .orElseThrow(() -> new EmpleadoNotFoundException(query.id()));
    }
}
