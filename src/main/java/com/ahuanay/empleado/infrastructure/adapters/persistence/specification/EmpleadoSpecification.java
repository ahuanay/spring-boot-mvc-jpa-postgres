package com.ahuanay.empleado.infrastructure.adapters.persistence.specification;

import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosQuery;
import com.ahuanay.empleado.common.infrastructure.adapters.persistence.JpaQueryUtils;
import com.ahuanay.empleado.infrastructure.adapters.persistence.entity.EmpleadoEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class EmpleadoSpecification {

    private static final String FIELD_NOMBRE = "nombre";
    private static final String FIELD_APELLIDO = "apellido";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_PUESTO = "puesto";
    private static final String FIELD_SALARIO = "salario";
    private static final String FIELD_FECHA_INGRESO = "fechaIngreso";

    private EmpleadoSpecification() {
    }

    public static Specification<EmpleadoEntity> conFiltros(GetAllEmpleadosQuery query) {
        return (root, queryCriteria, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            agregarFiltrosTexto(predicates, root, cb, query);
            agregarFiltrosAtributosExactos(predicates, root, cb, query);
            agregarFiltrosRangos(predicates, root, cb, query);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void agregarFiltrosTexto(List<Predicate> predicates, Root<EmpleadoEntity> root, CriteriaBuilder cb, GetAllEmpleadosQuery query) {
        agregarFiltroLike(predicates, root, cb, FIELD_NOMBRE, query.nombre());
        agregarFiltroLike(predicates, root, cb, FIELD_APELLIDO, query.apellido());
        agregarFiltroLike(predicates, root, cb, FIELD_EMAIL, query.email());
    }

    private static void agregarFiltrosAtributosExactos(List<Predicate> predicates, Root<EmpleadoEntity> root, CriteriaBuilder cb, GetAllEmpleadosQuery query) {
        if (query.puesto() != null) {
            predicates.add(cb.equal(root.get(FIELD_PUESTO), query.puesto()));
        }
    }

    private static void agregarFiltrosRangos(List<Predicate> predicates, Root<EmpleadoEntity> root, CriteriaBuilder cb, GetAllEmpleadosQuery query) {
        if (query.salarioMinimo() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(FIELD_SALARIO), query.salarioMinimo()));
        }
        if (query.salarioMaximo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(FIELD_SALARIO), query.salarioMaximo()));
        }
        if (query.fechaIngresoDesde() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(FIELD_FECHA_INGRESO), query.fechaIngresoDesde()));
        }
        if (query.fechaIngresoHasta() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(FIELD_FECHA_INGRESO), query.fechaIngresoHasta()));
        }
    }

    private static void agregarFiltroLike(List<Predicate> predicates, Root<EmpleadoEntity> root, CriteriaBuilder cb, String atributo, String valor) {
        if (valor != null && !valor.isBlank()) {
            String patron = "%" + JpaQueryUtils.escaparLike(valor.toLowerCase()) + "%";
            predicates.add(cb.like(cb.lower(root.get(atributo)), patron, '\\'));
        }
    }
}