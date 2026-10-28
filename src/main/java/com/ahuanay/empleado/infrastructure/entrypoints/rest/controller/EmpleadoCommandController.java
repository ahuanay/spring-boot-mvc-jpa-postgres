package com.ahuanay.empleado.infrastructure.entrypoints.rest.controller;

import com.ahuanay.empleado.application.command.create.CreateEmpleadoCommand;
import com.ahuanay.empleado.application.command.create.CreateEmpleadoHandler;
import com.ahuanay.empleado.application.command.delete.DeleteEmpleadoCommand;
import com.ahuanay.empleado.application.command.delete.DeleteEmpleadoHandler;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoCommand;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoHandler;
import com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto.ErrorRestResponse;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestRequest;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.mapper.EmpleadoRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
            description = "Empleado creado exitosamente en el sistema. Retorna el identificador asignado.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Long.class, example = "105")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Petición inválida por violación de invariantes del dominio (ej. correo duplicado o salario negativo) o por datos malformados en el DTO.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorRestResponse.class),
                examples = @ExampleObject(
                    name = "EmailDuplicadoException",
                    value = "{\"timestamp\": \"2026-10-27T14:30:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Ya existe un empleado activo con el email: ahuanay@email.com\", \"path\": \"/api/v1/empleados\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto de unicidad detectado por la base de datos durante una escritura concurrente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorRestResponse.class)
            )
        )
    })
    public ResponseEntity<Long> create(
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
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoId);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Actualizar datos de un empleado",
        description = "Modifica la información de un empleado existente identificándolo por su ID. Verifica la existencia previa del registro y la unicidad del correo respetando su propia identidad."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Empleado actualizado correctamente. La respuesta no contiene cuerpo."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos o reglas de negocio violadas durante la actualización.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorRestResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró ningún empleado activo con el ID proporcionado.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorRestResponse.class),
                examples = @ExampleObject(
                    name = "EmpleadoNotFound",
                    value = "{\"timestamp\": \"2026-10-27T14:30:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"No se encontró el empleado con el ID: 99\", \"path\": \"/api/v1/empleados/99\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto de unicidad detectado por la base de datos durante una escritura concurrente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorRestResponse.class)
            )
        )
    })
    public ResponseEntity<Void> update(
        @Parameter(description = "Identificador único (ID) del empleado a actualizar.", example = "105", required = true)
        @PathVariable Long id,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Objeto con los campos actualizados del empleado.",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EmpleadoRestRequest.class),
                examples = @ExampleObject(
                    name = "EmpleadoSenior",
                    summary = "Actualización de los datos del empleado",
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
        UpdateEmpleadoCommand command = restMapper.toUpdateCommand(id, request);
        updateHandler.handle(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Eliminar empleado (Soft Delete)",
        description = "Ejecuta un borrado lógico estableciendo la marca de tiempo `deleted_at`. El registro deja de ser consultable en las búsquedas activas, liberando el correo único para futuros registros."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Empleado dado de baja exitosamente. La respuesta no contiene cuerpo."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "El empleado especificado no existe o ya fue eliminado anteriormente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorRestResponse.class)
            )
        )
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID del empleado que será dado de baja.", example = "105", required = true)
        @PathVariable Long id
    ) {
        DeleteEmpleadoCommand command = new DeleteEmpleadoCommand(id);
        deleteHandler.handle(command);
        return ResponseEntity.noContent().build();
    }
}