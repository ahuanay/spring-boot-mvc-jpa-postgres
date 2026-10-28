package com.ahuanay.empleado.infrastructure.entrypoints.rest.controller;

import com.ahuanay.empleado.application.query.getall.EmpleadoSummaryReadModel;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosHandler;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosQuery;
import com.ahuanay.empleado.application.query.getbyid.EmpleadoDetailReadModel;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoHandler;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoQuery;
import com.ahuanay.empleado.common.application.pagination.PagedReadModel;
import com.ahuanay.empleado.common.application.pagination.QueryPagination;
import com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto.ErrorRestResponse;
import com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto.PageRestResponse;
import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.dto.EmpleadoRestResponse;
import com.ahuanay.empleado.infrastructure.entrypoints.rest.mapper.EmpleadoRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/empleados")
@RequiredArgsConstructor
@Tag(
    name = "Empleados - Consultas",
    description = "Puntos de entrada de lectura (CQRS - Query Side). Los handlers de consulta devuelven modelos de lectura desacoplados del agregado de escritura."
)
public class EmpleadoQueryController {

    private final GetByIdEmpleadoHandler getByIdHandler;
    private final GetAllEmpleadosHandler getAllHandler;
    private final EmpleadoRestMapper restMapper;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Obtener detalle de un empleado por ID",
        description = "Recupera la información completa y detallada de un empleado activo a través de su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Detalle del empleado recuperado con éxito.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EmpleadoRestResponse.class),
                examples = @ExampleObject(
                    name = "EmpleadoSenior",
                    summary = "Empleado activo con puesto senior",
                    value = """
                        {
                            "id": 105,
                            "nombre": "Anderson Bryam",
                            "apellido": "Huanay Carhuaricra",
                            "email": "ahuanay@email.com",
                            "puesto": "DESARROLLADOR_SENIOR",
                            "salario": 4500.00,
                            "fechaIngreso": "2026-10-26"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Empleado no encontrado o inactivo por borrado lógico.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorRestResponse.class)
            )
        )
    })
    public ResponseEntity<EmpleadoRestResponse> getById(
        @Parameter(description = "ID único del empleado a consultar.", example = "105", required = true)
        @PathVariable Long id
    ) {
        GetByIdEmpleadoQuery query = new GetByIdEmpleadoQuery(id);
        EmpleadoDetailReadModel readModel = getByIdHandler.handle(query);
        return ResponseEntity.ok(restMapper.toRestResponse(readModel));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Listado paginado de empleados con filtros múltiples",
        description = "Consulta la lista de empleados mediante especificaciones dinámicas. Permite filtrado opcional por atributos, búsqueda en rangos (salario y fechas) y paginación personalizada (usar `size = -1` para obtener todos los registros sin paginar)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Página de datos obtenida exitosamente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PageRestResponse.class),
                examples = @ExampleObject(
                    name = "PaginaEmpleados",
                    summary = "Primera página con un empleado activo",
                    value = """
                        {
                            "content": [
                                {
                                    "id": 105,
                                    "nombre": "Anderson Bryam",
                                    "apellido": "Huanay Carhuaricra",
                                    "email": "ahuanay@email.com",
                                    "puesto": "DESARROLLADOR_SENIOR"
                                }
                            ],
                            "pageNumber": 0,
                            "pageSize": 10,
                            "totalElements": 1,
                            "totalPages": 1,
                            "isLast": true
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Filtro de búsqueda con formato no válido o parámetro de ordenamiento no soportado.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorRestResponse.class),
                examples = @ExampleObject(
                    name = "ParametroInvalido",
                    value = "{\"timestamp\": \"2026-10-27T14:30:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"El parámetro 'sortBy' con valor 'salarioInvalido' no es válido. Valores permitidos: apellido, email, fechaIngreso, id, nombre, puesto, salario.\", \"path\": \"/api/v1/empleados\"}"
                )
            )
        )
    })
    public ResponseEntity<PageRestResponse<EmpleadoRestResponse>> getAll(
        @Parameter(description = "Número de página a consultar (Base cero).", example = "0")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Cantidad de registros por página. Utilice -1 para ignorar la paginación y traer todos los registros.", example = "10")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "Campo por el cual se ordenarán los resultados.", example = "id")
        @RequestParam(defaultValue = "id") String sortBy,

        @Parameter(description = "Sentido del ordenamiento.", example = "ASC", schema = @Schema(allowableValues = {"ASC", "DESC"}, defaultValue = "ASC"))
        @RequestParam(defaultValue = "ASC") String direction,

        @Parameter(description = "Filtro parcial o exacto por nombre.", example = "Anderson")
        @RequestParam(required = false) String nombre,

        @Parameter(description = "Filtro parcial o exacto por apellido.", example = "Huanay")
        @RequestParam(required = false) String apellido,

        @Parameter(description = "Filtro parcial por dirección de correo electrónico.", example = "ahuanay@email.com")
        @RequestParam(required = false) String email,

        @Parameter(description = "Filtro por el puesto de trabajo ocupado.", example = "DESARROLLADOR_SENIOR", schema = @Schema(implementation = Puesto.class))
        @RequestParam(required = false) Puesto puesto,

        @Parameter(description = "Límite inferior para búsqueda de salario.", example = "2500.00")
        @RequestParam(required = false) BigDecimal salarioMinimo,

        @Parameter(description = "Límite superior para búsqueda de salario.", example = "8000.00")
        @RequestParam(required = false) BigDecimal salarioMaximo,

        @Parameter(description = "Fecha inicial del rango de ingreso (YYYY-MM-DD).", example = "2024-01-01")
        @RequestParam(required = false) LocalDate fechaIngresoDesde,

        @Parameter(description = "Fecha final del rango de ingreso (YYYY-MM-DD).", example = "2026-12-31")
        @RequestParam(required = false) LocalDate fechaIngresoHasta
    ) {
        QueryPagination queryPagination = new QueryPagination(page, size, sortBy, direction);

        GetAllEmpleadosQuery query = new GetAllEmpleadosQuery(
            queryPagination,
            nombre,
            apellido,
            email,
            puesto,
            salarioMinimo,
            salarioMaximo,
            fechaIngresoDesde,
            fechaIngresoHasta
        );

        PagedReadModel<EmpleadoSummaryReadModel> readModel = getAllHandler.handle(query);
        return ResponseEntity.ok(restMapper.toPageRest(readModel));
    }
}
