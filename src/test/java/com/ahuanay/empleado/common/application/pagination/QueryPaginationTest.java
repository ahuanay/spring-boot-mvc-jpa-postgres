package com.ahuanay.empleado.common.application.pagination;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class QueryPaginationTest {

    @Test
    @DisplayName("Debería crear QueryPagination con valores válidos correctamente")
    void deberiaCrearQueryPaginationConValoresValidos() {
        // Arrange & Act
        QueryPagination pagination = new QueryPagination(1, 20, "nombre", "DESC");

        // Assert
        assertEquals(1, pagination.page());
        assertEquals(20, pagination.size());
        assertEquals("nombre", pagination.sortBy());
        assertEquals("DESC", pagination.direction());
        assertFalse(pagination.isUnpaged());
    }

    @Test
    @DisplayName("Debería asignar size por defecto (10) cuando size es 0 o menor a -1")
    void deberiaAsignarValoresPorDefectoCuandoSizeEsInvalido() {
        // Arrange & Act
        QueryPagination paginationZero = new QueryPagination(0, 0, "id", "ASC");
        QueryPagination paginationNegative = new QueryPagination(0, -2, "id", "ASC");

        // Assert
        assertEquals(10, paginationZero.size());
        assertEquals(10, paginationNegative.size());
        assertFalse(paginationZero.isUnpaged());
        assertFalse(paginationNegative.isUnpaged());
    }

    @Test
    @DisplayName("Debería permitir size = -1 reconociendo que no está paginado (unpaged)")
    void deberiaPermitirSizeUnpaged() {
        // Arrange & Act
        QueryPagination pagination = new QueryPagination(0, -1, "id", "ASC");

        // Assert
        assertEquals(-1, pagination.size());
        assertTrue(pagination.isUnpaged());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Debería asignar 'id' como sortBy por defecto cuando es nulo o en blanco")
    void deberiaAsignarSortByPorDefecto(String sortByInvalido) {
        // Arrange & Act
        QueryPagination pagination = new QueryPagination(0, 10, sortByInvalido, "ASC");

        // Assert
        assertEquals("id", pagination.sortBy());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Debería asignar 'ASC' como direction por defecto cuando es nulo o en blanco")
    void deberiaAsignarDirectionPorDefecto(String directionInvalida) {
        // Arrange & Act
        QueryPagination pagination = new QueryPagination(0, 10, "id", directionInvalida);

        // Assert
        assertEquals("ASC", pagination.direction());
    }
}