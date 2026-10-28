package com.ahuanay.empleado.infrastructure.entrypoints.rest.exception;

import com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto.ErrorRestResponse;
import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;
import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EmpleadoExceptionHandler {

    @ExceptionHandler(EmpleadoNotFoundException.class)
    public ResponseEntity<ErrorRestResponse> handleEmpleadoNotFoundException(EmpleadoNotFoundException ex) {
        ErrorRestResponse response = ErrorRestResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .notFoundIds(ex.getNotFoundIds())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(EmpleadoDomainException.class)
    public ResponseEntity<ErrorRestResponse> handleEmpleadoDomainException(EmpleadoDomainException ex) {
        ErrorRestResponse response = ErrorRestResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}