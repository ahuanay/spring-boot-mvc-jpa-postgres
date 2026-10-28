package com.ahuanay.empleado.application.query.getbyid;

import com.ahuanay.empleado.application.query.EmpleadoQueryRepository;
import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class GetByIdEmpleadoHandler {

    private final EmpleadoQueryRepository empleadoQueryRepository;

    public GetByIdEmpleadoHandler(EmpleadoQueryRepository empleadoQueryRepository) {
        this.empleadoQueryRepository = empleadoQueryRepository;
    }

    @Transactional(readOnly = true)
    public GetByIdEmpleadoResult handle(GetByIdEmpleadoQuery query) {
        return empleadoQueryRepository.findResultById(query.id())
            .orElseThrow(() -> new EmpleadoNotFoundException(query.id()));
    }
}
