package com.banco.intranet.news.repository;

import com.banco.intranet.news.entity.NoticiaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones de Noticia
 */
@Repository
public interface NoticiaRepository extends JpaRepository<NoticiaEntity, Long> {

    Page<NoticiaEntity> findByActivaTrueAndFechaPublicacionBeforeAndFechaExpiracionAfterOrFechaExpiracionIsNull(
        LocalDateTime fechaPublicacion, LocalDateTime fechaExpiracion, Pageable pageable);

    Page<NoticiaEntity> findByActiva(Boolean activa, Pageable pageable);

    List<NoticiaEntity> findByActivaTrueAndCategoriaOrderByFechaPublicacionDesc(String categoria);

    Page<NoticiaEntity> findByTituloContainingIgnoreCaseAndActiva(String titulo, Boolean activa, Pageable pageable);

    long countByActivaTrue();
}
