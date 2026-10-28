package com.ahuanay.empleado.infrastructure.entrypoints.rest.mapper;

import com.ahuanay.empleado.application.command.create.CreateEmpleadoCommand;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoCommand;
import com.ahuanay.empleado.application.query.getall.EmpleadoSummaryReadModel;
import com.ahuanay.empleado.application.query.getbyid.EmpleadoDetailReadModel;
import com.ahuanay.empleado.common.application.pagination.PagedReadModel;
import com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto.PageRestResponse;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestRequest;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmpleadoRestMapper {

    CreateEmpleadoCommand toCreateCommand(EmpleadoRestRequest request);

    @Mapping(target = "id", source = "id")
    UpdateEmpleadoCommand toUpdateCommand(Long id, EmpleadoRestRequest request);

    EmpleadoRestResponse toRestResponse(EmpleadoDetailReadModel readModel);

    EmpleadoRestResponse toRestResponse(EmpleadoSummaryReadModel readModel);

    default PageRestResponse<EmpleadoRestResponse> toPageRest(PagedReadModel<EmpleadoSummaryReadModel> paged) {
        return new PageRestResponse<>(
            paged.content().stream().map(this::toRestResponse).toList(),
            paged.pageNumber(),
            paged.pageSize(),
            paged.totalElements(),
            paged.totalPages(),
            paged.isLast()
        );
    }
}
