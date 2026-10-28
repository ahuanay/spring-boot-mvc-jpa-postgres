package com.ahuanay.empleado.infrastructure.entrypoints.rest.mapper;

import com.ahuanay.empleado.application.command.create.CreateEmpleadoCommand;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoCommand;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosResult;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoResult;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestRequest;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmpleadoRestMapper {

    CreateEmpleadoCommand toCreateCommand(EmpleadoRestRequest request);

    @Mapping(target = "id", source = "id")
    UpdateEmpleadoCommand toUpdateCommand(Long id, EmpleadoRestRequest request);

    EmpleadoRestResponse toRestResponse(GetByIdEmpleadoResult readModel);

    EmpleadoRestResponse toRestResponse(GetAllEmpleadosResult readModel);
}