package com.ahuanay.empleado.infrastructure.config;

import com.ahuanay.empleado.application.command.create.CreateEmpleadoHandler;
import com.ahuanay.empleado.application.command.delete.DeleteEmpleadoHandler;
import com.ahuanay.empleado.application.command.update.UpdateEmpleadoHandler;
import com.ahuanay.empleado.application.query.EmpleadoQueryRepository;
import com.ahuanay.empleado.application.query.getall.GetAllEmpleadosHandler;
import com.ahuanay.empleado.application.query.getbyid.GetByIdEmpleadoHandler;
import com.ahuanay.empleado.domain.repository.EmpleadoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateEmpleadoHandler createEmpleadoHandler(EmpleadoRepository empleadoRepository) {
        return new CreateEmpleadoHandler(empleadoRepository);
    }

    @Bean
    public UpdateEmpleadoHandler updateEmpleadoHandler(EmpleadoRepository empleadoRepository) {
        return new UpdateEmpleadoHandler(empleadoRepository);
    }

    @Bean
    public DeleteEmpleadoHandler deleteEmpleadoHandler(EmpleadoRepository empleadoRepository) {
        return new DeleteEmpleadoHandler(empleadoRepository);
    }

    @Bean
    public GetByIdEmpleadoHandler getByIdEmpleadoHandler(EmpleadoQueryRepository empleadoQueryRepository) {
        return new GetByIdEmpleadoHandler(empleadoQueryRepository);
    }

    @Bean
    public GetAllEmpleadosHandler getAllEmpleadosHandler(EmpleadoQueryRepository empleadoQueryRepository) {
        return new GetAllEmpleadosHandler(empleadoQueryRepository);
    }
}
