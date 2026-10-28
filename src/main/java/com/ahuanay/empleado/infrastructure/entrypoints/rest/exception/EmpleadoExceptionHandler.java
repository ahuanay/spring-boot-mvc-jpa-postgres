package com.ahuanay.empleado.infrastructure.entrypoints.rest.exception;

import com.ahuanay.empleado.domain.exception.EmpleadoDomainException;
import com.ahuanay.empleado.domain.exception.EmpleadoNotFoundException;
import com.ahuanay.mvccommon.infrastructure.entrypoints.rest.dto.RestResponse;
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
    public ResponseEntity<RestResponse<Object>> handleEmpleadoNotFoundException(EmpleadoNotFoundException ex) {
        RestResponse<Object> response = RestResponse.error(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            ex.getNotFoundIds()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(EmpleadoDomainException.class)
    public ResponseEntity<RestResponse<Object>> handleEmpleadoDomainException(EmpleadoDomainException ex) {
        RestResponse<Object> response = RestResponse.error(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}