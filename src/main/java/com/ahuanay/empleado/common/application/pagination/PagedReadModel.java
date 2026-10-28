package com.ahuanay.empleado.common.application.pagination;

import java.util.List;

public record PagedReadModel<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean isLast
) {
}