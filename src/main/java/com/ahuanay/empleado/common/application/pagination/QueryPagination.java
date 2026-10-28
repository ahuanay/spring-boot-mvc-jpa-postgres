package com.ahuanay.empleado.common.application.pagination;

public record QueryPagination(
    int page,
    int size,
    String sortBy,
    String direction
) {
    public QueryPagination {
        if (size == 0 || size < -1) {
            size = 10;
        }
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
        if (direction == null || direction.isBlank()) {
            direction = "ASC";
        }
    }

    public boolean isUnpaged() {
        return this.size == -1;
    }
}