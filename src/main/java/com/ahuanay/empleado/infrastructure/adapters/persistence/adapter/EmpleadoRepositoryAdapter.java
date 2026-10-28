package com.ahuanay.empleado.infrastructure.adapters.persistence.adapter;

import com.ahuanay.empleado.domain.model.Empleado;
import com.ahuanay.empleado.domain.repository.EmpleadoRepository;
import com.ahuanay.empleado.infrastructure.adapters.persistence.entity.EmpleadoEntity;
import com.ahuanay.empleado.infrastructure.adapters.persistence.mapper.EmpleadoMapper;
import com.ahuanay.empleado.infrastructure.adapters.persistence.repository.EmpleadoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmpleadoRepositoryAdapter implements EmpleadoRepository {

    private static final String USUARIO_SISTEMA = "SYSTEM";

    private final EmpleadoJpaRepository jpaRepository;
    private final EmpleadoMapper mapper;

    @Override
    public Empleado save(Empleado empleado) {
        EmpleadoEntity entity = mapper.toEntity(empleado);
        EmpleadoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Empleado> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.setDeletedAt(LocalDateTime.now());
            entity.setDeletedBy(USUARIO_SISTEMA);
            jpaRepository.save(entity);
        });
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return jpaRepository.existsByEmailAndIdNot(email, id);
    }
}
