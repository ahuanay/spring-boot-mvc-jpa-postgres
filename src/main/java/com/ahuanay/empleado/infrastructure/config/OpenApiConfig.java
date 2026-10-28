package com.ahuanay.empleado.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API de Gestión de Empleados",
        version = "1.0.0",
        description = "Documentación oficial de los microservicios y módulos de gestión de empleados bajo Arquitectura Hexagonal, CQRS y DDD."
    ),
    servers = {
        @Server(url = "/", description = "Servidor Local / Entorno Actual")
    }
)
public class OpenApiConfig {

}