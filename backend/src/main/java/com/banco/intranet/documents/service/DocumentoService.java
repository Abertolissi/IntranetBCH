package com.banco.intranet.documents.service;

import com.banco.intranet.common.exception.AppException;
import com.banco.intranet.documents.dto.DocumentoDTO;
import com.banco.intranet.documents.entity.DocumentoEntity;
import com.banco.intranet.documents.repository.DocumentoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de gestión de documentos.
 */
@Slf4j
@Service
@Transactional
public class DocumentoService {

    private final DocumentoRepository documentoRepository;

    public DocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public DocumentoDTO obtenerPorId(Long id) {
        DocumentoEntity documento = documentoRepository.findById(id)
                .orElseThrow(() -> new AppException("DOC_NOT_FOUND", "Documento no encontrado", 404));
        return mapearADTO(documento);
    }

    public Page<DocumentoDTO> obtenerActivos(Pageable pageable) {
        return documentoRepository.findByActivo(true, pageable).map(this::mapearADTO);
    }

    public Page<DocumentoDTO> buscarPorTitulo(String titulo, Pageable pageable) {
        return documentoRepository.findByTituloContainingIgnoreCaseAndActivo(titulo, true, pageable)
                .map(this::mapearADTO);
    }

    public DocumentoDTO crear(DocumentoDTO dto) {
        DocumentoEntity documento = new DocumentoEntity();
        mapearAEntidad(dto, documento);
        documento.setActivo(true);
        documento.setNumeroDescargas(0);
        if (documento.getVersion() == null || documento.getVersion().isBlank()) {
            documento.setVersion("1.0");
        }
        if (documento.getUsuarioCreacion() == null || documento.getUsuarioCreacion().isBlank()) {
            documento.setUsuarioCreacion("SYSTEM");
        }

        DocumentoEntity guardado = documentoRepository.save(documento);
        log.info("Documento creado: {}", guardado.getId());
        return mapearADTO(guardado);
    }

    public DocumentoDTO actualizar(Long id, DocumentoDTO dto) {
        DocumentoEntity documento = documentoRepository.findById(id)
                .orElseThrow(() -> new AppException("DOC_NOT_FOUND", "Documento no encontrado", 404));

        mapearAEntidad(dto, documento);
        DocumentoEntity actualizado = documentoRepository.save(documento);
        log.info("Documento actualizado: {}", id);
        return mapearADTO(actualizado);
    }

    public void desactivar(Long id) {
        DocumentoEntity documento = documentoRepository.findById(id)
                .orElseThrow(() -> new AppException("DOC_NOT_FOUND", "Documento no encontrado", 404));
        documento.setActivo(false);
        documentoRepository.save(documento);
        log.info("Documento desactivado: {}", id);
    }

    public void incrementarDescargas(Long id) {
        DocumentoEntity documento = documentoRepository.findById(id)
                .orElseThrow(() -> new AppException("DOC_NOT_FOUND", "Documento no encontrado", 404));
        documento.setNumeroDescargas((documento.getNumeroDescargas() == null ? 0 : documento.getNumeroDescargas()) + 1);
        documentoRepository.save(documento);
    }

    private DocumentoDTO mapearADTO(DocumentoEntity documento) {
        return DocumentoDTO.builder()
                .id(documento.getId())
                .titulo(documento.getTitulo())
                .descripcion(documento.getDescripcion())
                .tipo(documento.getTipo())
                .clasificacion(documento.getClasificacion())
                .activo(documento.getActivo())
                .numeroDescargas(documento.getNumeroDescargas())
                .autorNombre(documento.getAutorNombre())
                .departamento(documento.getDepartamento())
                .etiquetas(documento.getEtiquetas())
                .version(documento.getVersion())
                .fechaVigenciaInicio(documento.getFechaVigenciaInicio())
                .fechaVigenciaFin(documento.getFechaVigenciaFin())
                .fechaCreacion(documento.getFechaCreacion())
                .fechaActualizacion(documento.getFechaActualizacion())
                .rutaArchivo(documento.getRutaArchivo())
                .tamano(documento.getTamanio())
                .build();
    }

    private void mapearAEntidad(DocumentoDTO dto, DocumentoEntity entidad) {
        entidad.setTitulo(dto.getTitulo());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setTipo(dto.getTipo());
        entidad.setClasificacion(dto.getClasificacion());
        entidad.setRutaArchivo(dto.getRutaArchivo());
        entidad.setExtension(obtenerExtension(dto.getRutaArchivo()));
        entidad.setTamanio(dto.getTamano() == null ? 0L : dto.getTamano());
        entidad.setAutorNombre(dto.getAutorNombre());
        entidad.setDepartamento(dto.getDepartamento());
        entidad.setEtiquetas(dto.getEtiquetas());
        entidad.setFechaVigenciaInicio(dto.getFechaVigenciaInicio());
        entidad.setFechaVigenciaFin(dto.getFechaVigenciaFin());

        if (dto.getVersion() != null && !dto.getVersion().isBlank()) {
            entidad.setVersion(dto.getVersion());
        }

        if (dto.getActivo() != null) {
            entidad.setActivo(dto.getActivo());
        }
    }

    private String obtenerExtension(String rutaArchivo) {
        if (rutaArchivo == null || !rutaArchivo.contains(".")) {
            return "bin";
        }
        int idx = rutaArchivo.lastIndexOf('.');
        return rutaArchivo.substring(idx + 1).toLowerCase();
    }
}
