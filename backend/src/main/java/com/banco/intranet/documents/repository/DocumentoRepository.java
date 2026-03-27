package com.banco.intranet.documents.repository;

import com.banco.intranet.documents.entity.DocumentoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para operaciones de Documento
 */
@Repository
public interface DocumentoRepository extends JpaRepository<DocumentoEntity, Long> {

    Page<DocumentoEntity> findByActivo(Boolean activo, Pageable pageable);

    Page<DocumentoEntity> findByClasificacion(String clasificacion, Pageable pageable);

    Page<DocumentoEntity> findByDepartamento(String departamento, Pageable pageable);

    Page<DocumentoEntity> findByTituloContainingIgnoreCaseAndActivo(String titulo, Boolean activo, Pageable pageable);

    Page<DocumentoEntity> findByTipo(String tipo, Pageable pageable);

    List<DocumentoEntity> findByEtiquetasContainingIgnoreCaseAndActivo(String etiqueta, Boolean activo);

    long countByActivoTrue();
}
