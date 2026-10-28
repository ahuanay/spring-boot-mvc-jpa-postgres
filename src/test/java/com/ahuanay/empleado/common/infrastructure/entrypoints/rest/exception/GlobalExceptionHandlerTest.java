package com.ahuanay.empleado.common.infrastructure.entrypoints.rest.exception;

import com.ahuanay.empleado.domain.exception.ParametroInvalidoException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("Debería manejar MethodArgumentNotValidException y retornar 400 con los errores por campo")
    void deberiaManejarValidationException() throws Exception {
        // Arrange
        String jsonBody = "{\"nombre\":\"\"}";

        // Act & Assert
        mockMvc.perform(post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.message").value("La validación de los campos ha fallado."))
            .andExpect(jsonPath("$.errors.nombre").exists());
    }

    @Test
    @DisplayName("Debería manejar HttpMessageNotReadableException y retornar 400 por JSON malformado")
    void deberiaManejarHttpMessageNotReadable() throws Exception {
        // Arrange
        String jsonMalformado = "{\"nombre\": ";

        // Act & Assert
        mockMvc.perform(post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMalformado))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("El formato del JSON es inválido o contiene valores no permitidos."));
    }

    @Test
    @DisplayName("Debería manejar ParametroInvalidoException y retornar 400 con el mensaje específico")
    void deberiaManejarParametroInvalidoException() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/test/param-invalido"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("El parámetro 'sortBy' no es válido."));
    }

    @Test
    @DisplayName("Debería manejar DataIntegrityViolationException y retornar 409 Conflict")
    void deberiaManejarDataIntegrityViolationException() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/test/conflicto"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"))
            .andExpect(jsonPath("$.message")
                .value("La operación entra en conflicto con una restricción de integridad de datos."));
    }

    @Test
    @DisplayName("Debería manejar Exception no controlada y retornar 500 Internal Server Error")
    void deberiaManejarGlobalException() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/test/error-interno"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("Ocurrió un error inesperado en el servidor."));
    }
    
    @RestController
    @RequestMapping("/test")
    static class TestController {

        @PostMapping("/validation")
        public void testValidation(@Valid @RequestBody TestDto dto) {
        }

        @GetMapping("/param-invalido")
        public void testParamInvalido() {
            throw new ParametroInvalidoException("El parámetro 'sortBy' no es válido.");
        }

        @GetMapping("/conflicto")
        public void testConflicto() {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }

        @GetMapping("/error-interno")
        public void testErrorInterno() {
            throw new RuntimeException("Error inesperado de prueba");
        }
    }

    static class TestDto {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
}