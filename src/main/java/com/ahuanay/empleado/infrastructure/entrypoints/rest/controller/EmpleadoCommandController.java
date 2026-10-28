package com.ahuanay.empleado.infrastructure.entrypoints.rest.controller;

import com.ahuanay.empleado.application.command.create.CreateEmpleadoCommand;
import com.ahuanay.empleado.application.command.create.CreateEmpleadoHandler;
import com.ahuanay.empleado.application.command.delete.DeleteEmpleadoCommand;
import com.ahuanay.empleado.application.command.delete.DeleteEmpleadoHandler;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoCommand;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoHandler;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestRequest;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.mapper.EmpleadoRestMapper;
import com.ahuanay.mvccommon.infrastructure.entrypoints.rest.dto.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Import directo sin conflictos
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/empleados")
@RequiredArgsConstructor
@Tag(
    name = "Empleados - Comandos",
    description = "Puntos de entrada de escritura para la gestión de empleados (CQRS - Mutation Side). Permite la creación, actualización y borrado lógico."
)
public class EmpleadoCommandController {

    private final CreateEmpleadoHandler createHandler;
    private final UpdateEmpleadoHandler updateHandler;
    private final DeleteEmpleadoHandler deleteHandler;
    private final EmpleadoRestMapper restMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Crear un nuevo empleado",
        description = "Ejecuta el caso de uso para crear un empleado. Aplica validaciones de formato (Fail-Fast) y verifica que el correo electrónico no esté registrado por ningún empleado activo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Empleado creado exitosamente en el sistema. Retorna el identificador asignado dentro del RestResponse.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RestResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Petición inválida por violación de invariantes del dominio.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RestResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto de unicidad detectado.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RestResponse.class)
            )
        )
    })
    public ResponseEntity<RestResponse<Long>> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Estructura JSON con los datos requeridos para registrar al empleado.",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EmpleadoRestRequest.class),
                examples = @ExampleObject(
                    name = "EmpleadoSenior",
                    summary = "Alta de un empleado con puesto senior",
                    value = """
                        {
                            "nombre": "Anderson Bryam",
                            "apellido": "Huanay Carhuaricra",
                            "email": "ahuanay@email.com",
                            "puesto": "DESARROLLADOR_SENIOR",
                            "salario": 4500,
                            "fechaIngreso": "2026-10-26"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody EmpleadoRestRequest request
    ) {
        CreateEmpleadoCommand command = restMapper.toCreateCommand(request);
        Long empleadoId = createHandler.handle(command);

        RestResponse<Long> response = RestResponse.success(
            HttpStatus.CREATED.value(),
            "Empleado creado exitosamente",
            empleadoId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Actualizar datos de un empleado",
        description = "Modifica la información de un empleado existente identificándolo por su ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Empleado actualizado correctamente."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró el empleado.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestResponse.class))
        )
    })
    public ResponseEntity<RestResponse<Void>> update(
        @Parameter(description = "Identificador único (ID) del empleado a actualizar.", example = "105", required = true)
        @PathVariable Long id,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Objeto con los campos actualizados del empleado.",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EmpleadoRestRequest.class)
            )
        )
        @Valid @RequestBody EmpleadoRestRequest request
    ) {
        UpdateEmpleadoCommand command = restMapper.toUpdateCommand(id, request);
        updateHandler.handle(command);

        RestResponse<Void> response = RestResponse.success(
            HttpStatus.OK.value(),
            "Empleado actualizado correctamente"
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Eliminar empleado (Soft Delete)",
        description = "Ejecuta un borrado lógico estableciendo la marca de tiempo `deleted_at`."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Empleado dado de baja exitosamente."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "El empleado especificado no existe.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestResponse.class))
        )
    })
    public ResponseEntity<RestResponse<Void>> delete(
        @Parameter(description = "ID del empleado que será dado de baja.", example = "105", required = true)
        @PathVariable Long id
    ) {
        DeleteEmpleadoCommand command = new DeleteEmpleadoCommand(id);
        deleteHandler.handle(command);

        RestResponse<Void> response = RestResponse.success(
            HttpStatus.OK.value(),
            "Empleado dado de baja exitosamente"
        );

        return ResponseEntity.ok(response);
    }
}