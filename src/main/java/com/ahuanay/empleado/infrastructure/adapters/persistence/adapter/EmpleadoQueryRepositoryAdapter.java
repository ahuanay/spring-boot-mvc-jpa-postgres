package com.ahuanay.empleado.infrastructure.adapters.persistence.adapter;

import com.ahuanay.empleado.application.query.EmpleadoQueryRepository;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosQuery;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosResult;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoResult;
import com.ahuanay.empleado.infrastructure.adapters.persistence.entity.EmpleadoEntity;
import com.ahuanay.empleado.infrastructure.adapters.persistence.repository.EmpleadoJpaRepository;
import com.ahuanay.empleado.infrastructure.adapters.persistence.specification.EmpleadoSpecification;
import com.ahuanay.mvccommon.application.pagination.PagedResult;
import com.ahuanay.mvccommon.application.pagination.Pagination;
import com.ahuanay.mvccommon.infrastructure.adapters.persistence.JpaQueryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EmpleadoQueryRepositoryAdapter implements EmpleadoQueryRepository {

    private static final Set<String> CAMPOS_ORDENABLES = Set.of(
        "id", "nombre", "apellido", "email", "puesto", "salario", "fechaIngreso"
    );

    private final EmpleadoJpaRepository jpaRepository;

    @Override
    public Optional<GetByIdEmpleadoResult> findResultById(Long id) {
        return jpaRepository.findResultById(id);
    }

    @Override
    public PagedResult<GetAllEmpleadosResult> search(GetAllEmpleadosQuery query) {
        Pagination queryPagination = query.pageable();

        Sort sort = JpaQueryUtils.resolverSort(
            queryPagination.sortBy(),
            queryPagination.direction(),
            CAMPOS_ORDENABLES
        );

        Pageable pageable = queryPagination.isUnpaged()
            ? Pageable.unpaged(sort)
            : PageRequest.of(queryPagination.page(), queryPagination.size(), sort);

        Specification<EmpleadoEntity> specification = EmpleadoSpecification.conFiltros(query);
        Page<EmpleadoEntity> page = jpaRepository.findAll(specification, pageable);

        return new PagedResult<>(
            page.getContent().stream().map(this::toResult).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }

    private GetAllEmpleadosResult toResult(EmpleadoEntity entity) {
        return new GetAllEmpleadosResult(
            entity.getId(),
            entity.getNombre(),
            entity.getApellido(),
            entity.getEmail(),
            entity.getPuesto()
        );
    }
}
