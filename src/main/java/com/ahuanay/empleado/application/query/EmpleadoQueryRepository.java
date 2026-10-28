package com.ahuanay.empleado.application.query;

import com.ahuanay.empleado.application.query.getall.EmpleadoSummaryReadModel;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosQuery;
import com.ahuanay.empleado.application.query.getbyid.EmpleadoDetailReadModel;
import com.ahuanay.mvccommon.application.pagination.PagedReadModel;

import java.util.Optional;

public interface EmpleadoQueryRepository {

    Optional<EmpleadoDetailReadModel> findDetailById(Long id);

    PagedReadModel<EmpleadoSummaryReadModel> search(GetAllEmpleadosQuery query);
}
