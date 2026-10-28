package com.ahuanay.empleado.application.query.getall;

import com.ahuanay.empleado.application.query.EmpleadoQueryRepository;
import com.ahuanay.empleado.common.application.pagination.PagedReadModel;

public class GetAllEmpleadosHandler {

    private final EmpleadoQueryRepository empleadoQueryRepository;

    public GetAllEmpleadosHandler(EmpleadoQueryRepository empleadoQueryRepository) {
        this.empleadoQueryRepository = empleadoQueryRepository;
    }

    public PagedReadModel<EmpleadoSummaryReadModel> handle(GetAllEmpleadosQuery query) {
        return empleadoQueryRepository.search(query);
    }
}
