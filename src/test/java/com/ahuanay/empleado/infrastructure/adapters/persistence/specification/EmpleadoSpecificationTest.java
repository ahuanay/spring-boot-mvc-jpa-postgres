package com.ahuanay.empleado.infrastructure.adapters.persistence.specification;

import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosQuery;
import com.ahuanay.empleado.domain.enums.Puesto;
import com.ahuanay.empleado.infrastructure.adapters.persistence.entity.EmpleadoEntity;
import com.ahuanay.mvccommon.application.pagination.QueryPagination;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmpleadoSpecificationTest {

    private static Stream<Arguments> proveedorDeFiltrosSpecification() {
        QueryPagination pageableParams = new QueryPagination(0, 10, "nombre", "ASC");
        return Stream.of(
            Arguments.of(new GetAllEmpleadosQuery(pageableParams, null, null, null, null, null, null, null, null)),

            Arguments.of(new GetAllEmpleadosQuery(
                pageableParams,
                "Anderson",
                "Huanay",
                "anderson.ahuanay@example.com",
                Puesto.DESARROLLADOR_SENIOR,
                new BigDecimal("4000.00"),
                new BigDecimal("5000.00"),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )),

            Arguments.of(new GetAllEmpleadosQuery(
                pageableParams,
                "",
                "   ",
                "",
                null,
                null,
                null,
                null,
                null
            ))
        );
    }

    @ParameterizedTest
    @MethodSource("proveedorDeFiltrosSpecification")
    @DisplayName("Debería generar la especificación JPA correctamente evaluando diferentes combinaciones de filtros y ramas")
    void deberiaGenerarSpecificationConFiltrosExitosamente(GetAllEmpleadosQuery query) {
        // Arrange
        Specification<EmpleadoEntity> spec = EmpleadoSpecification.conFiltros(query);

        @SuppressWarnings("unchecked")
        Root<EmpleadoEntity> root = mock(Root.class);
        CriteriaQuery<?> queryCriteria = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Expression<String> pathMock = mock(Path.class);

        when(root.get(anyString())).thenReturn((Path) pathMock);
        when(cb.and(any(Predicate[].class))).thenReturn(mock(Predicate.class));
        when(cb.lower(any())).thenReturn(pathMock);
        when(cb.like(any(), anyString(), eq('\\'))).thenReturn(mock(Predicate.class));
        when(cb.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(cb.greaterThanOrEqualTo(any(), any(Comparable.class))).thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(any(), any(Comparable.class))).thenReturn(mock(Predicate.class));

        // Act
        Predicate resultado = spec.toPredicate(root, queryCriteria, cb);

        // Assert
        assertNotNull(resultado);
    }
}