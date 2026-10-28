package com.ahuanay.empleado.common.infrastructure.entrypoints.rest.exception;

import com.ahuanay.empleado.common.infrastructure.entrypoints.rest.dto.ErrorRestResponse;
import com.ahuanay.empleado.domain.exception.ParametroInvalidoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRestResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorRestResponse response = ErrorRestResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("La validación de los campos ha fallado.")
            .errors(fieldErrors)
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRestResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorRestResponse response = ErrorRestResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message("El formato del JSON es inválido o contiene valores no permitidos.")
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ParametroInvalidoException.class)
    public ResponseEntity<ErrorRestResponse> handleParametroInvalidoException(ParametroInvalidoException ex) {
        ErrorRestResponse response = ErrorRestResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRestResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos: {}", ex.getMostSpecificCause().getMessage());

        ErrorRestResponse response = ErrorRestResponse.builder()
            .status(HttpStatus.CONFLICT.value())
            .error("Conflict")
            .message("La operación entra en conflicto con una restricción de integridad de datos.")
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRestResponse> handleGlobalException(Exception ex) {
        log.error("Error interno no controlado: ", ex);

        ErrorRestResponse response = ErrorRestResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("Ocurrió un error inesperado en el servidor.")
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
