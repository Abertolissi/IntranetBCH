package com.banco.intranet.users.repository;

import com.banco.intranet.users.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para operaciones de Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    Optional<UsuarioEntity> findByNumeroEmpleado(String numeroEmpleado);

    Optional<UsuarioEntity> findByEmailOrNumeroEmpleado(String email, String numeroEmpleado);

    boolean existsByEmail(String email);

    boolean existsByNumeroEmpleado(String numeroEmpleado);

    long countByActivoTrue();
}
