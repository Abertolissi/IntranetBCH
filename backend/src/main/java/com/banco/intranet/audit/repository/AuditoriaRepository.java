package com.banco.intranet.audit.repository;

import com.banco.intranet.audit.entity.AuditoriaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones de Auditoría
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaEntity, Long> {

    Page<AuditoriaEntity> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<AuditoriaEntity> findByAccion(String accion, Pageable pageable);

    Page<AuditoriaEntity> findByModulo(String modulo, Pageable pageable);

    Page<AuditoriaEntity> findByResultado(String resultado, Pageable pageable);

    Page<AuditoriaEntity> findByFechaAccionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    List<AuditoriaEntity> findByUsuarioIdAndFechaAccionBetween(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
