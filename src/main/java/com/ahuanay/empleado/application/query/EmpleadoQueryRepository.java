package com.ahuanay.empleado.application.query;

import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosQuery;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosResult;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoResult;
import com.ahuanay.mvccommon.application.pagination.PagedReadModel;

import java.util.Optional;

public interface EmpleadoQueryRepository {

    Optional<GetByIdEmpleadoResult> findResultById(Long id);

    PagedReadModel<GetAllEmpleadosResult> search(GetAllEmpleadosQuery query);
}
