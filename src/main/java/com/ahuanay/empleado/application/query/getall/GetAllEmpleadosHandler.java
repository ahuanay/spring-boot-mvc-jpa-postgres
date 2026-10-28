package com.ahuanay.empleado.application.query.getall;

import com.ahuanay.empleado.application.query.EmpleadoQueryRepository;
import com.ahuanay.mvccommon.application.pagination.PagedReadModel;
import org.springframework.transaction.annotation.Transactional;

public class GetAllEmpleadosHandler {

    private final EmpleadoQueryRepository empleadoQueryRepository;

    public GetAllEmpleadosHandler(EmpleadoQueryRepository empleadoQueryRepository) {
        this.empleadoQueryRepository = empleadoQueryRepository;
    }

    @Transactional(readOnly = true)
    public PagedReadModel<GetAllEmpleadosResult> handle(GetAllEmpleadosQuery query) {
        return empleadoQueryRepository.search(query);
    }
}
