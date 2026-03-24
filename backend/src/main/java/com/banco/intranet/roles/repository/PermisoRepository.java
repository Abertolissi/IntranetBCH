package com.banco.intranet.roles.repository;

import com.banco.intranet.roles.entity.PermisoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Repositorio para operaciones de Permiso
 */
@Repository
public interface PermisoRepository extends JpaRepository<PermisoEntity, Long> {

    Optional<PermisoEntity> findByCodigo(String codigo);

    List<PermisoEntity> findByModulo(String modulo);

    boolean existsByCodigo(String codigo);
}
