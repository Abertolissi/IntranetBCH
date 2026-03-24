package com.banco.intranet.news.service;

import com.banco.intranet.common.exception.AppException;
import com.banco.intranet.news.dto.NoticiaDTO;
import com.banco.intranet.news.entity.NoticiaEntity;
import com.banco.intranet.news.repository.NoticiaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio de gestión de noticias
 */
@Slf4j
@Service
@Transactional
public class NoticiaService {

    @Autowired
    private NoticiaRepository noticiaRepository;

    /**
     * Obtiene una noticia por ID
     */
    public NoticiaDTO obtenerPorId(Long id) {
        NoticiaEntity noticia = noticiaRepository.findById(id)
            .orElseThrow(() -> new AppException("NEWS_NOT_FOUND", "Noticia no encontrada", 404));
        return mapearADTO(noticia);
    }

    /**
     * Obtiene todas las noticias activas
     */
    public Page<NoticiaDTO> obtenerActivas(Pageable pageable) {
        return noticiaRepository.findByActiva(true, pageable)
            .map(this::mapearADTO);
    }

    /**
     * Busca noticias por título
     */
    public Page<NoticiaDTO> buscar(String titulo, Pageable pageable) {
        return noticiaRepository.findByTituloContainingIgnoreCaseAndActiva(titulo, true, pageable)
            .map(this::mapearADTO);
    }

    /**
     * Crea una nueva noticia
     */
    public NoticiaDTO crear(NoticiaDTO dto, Long autorId, String autorNombre) {
        log.info("Creando nueva noticia: {}", dto.getTitulo());

        NoticiaEntity noticia = new NoticiaEntity();
        noticia.setTitulo(dto.getTitulo());
        noticia.setContenido(dto.getContenido());
        noticia.setActiva(true);
        noticia.setFechaPublicacion(LocalDateTime.now());
        noticia.setFechaExpiracion(dto.getFechaExpiracion());
        noticia.setAutorId(autorId);
        noticia.setAutorNombre(autorNombre);
        noticia.setPrioridad(dto.getPrioridad() != null ? dto.getPrioridad() : "NORMAL");
        noticia.setCategoria(dto.getCategoria());
        noticia.setImagenUrl(dto.getImagenUrl());
        noticia.setUsuarioCreacion(autorNombre);

        NoticiaEntity guardada = noticiaRepository.save(noticia);
        log.info("Noticia creada exitosamente: {}", guardada.getId());

        return mapearADTO(guardada);
    }

    /**
     * Actualiza una noticia
     */
    public NoticiaDTO actualizar(Long id, NoticiaDTO dto) {
        log.info("Actualizando noticia: {}", id);

        NoticiaEntity noticia = noticiaRepository.findById(id)
            .orElseThrow(() -> new AppException("NEWS_NOT_FOUND", "Noticia no encontrada", 404));

        noticia.setTitulo(dto.getTitulo());
        noticia.setContenido(dto.getContenido());
        noticia.setFechaExpiracion(dto.getFechaExpiracion());
        noticia.setPrioridad(dto.getPrioridad());
        noticia.setCategoria(dto.getCategoria());
        noticia.setImagenUrl(dto.getImagenUrl());

        NoticiaEntity actualizada = noticiaRepository.save(noticia);
        log.info("Noticia actualizada: {}", id);

        return mapearADTO(actualizada);
    }

    /**
     * Incrementa el contador de lecturas
     */
    public void incrementarLecturas(Long id) {
        NoticiaEntity noticia = noticiaRepository.findById(id)
            .orElseThrow(() -> new AppException("NEWS_NOT_FOUND", "Noticia no encontrada", 404));

        noticia.setNumeroLecturas(noticia.getNumeroLecturas() + 1);
        noticiaRepository.save(noticia);
    }

    /**
     * Desactiva una noticia
     */
    public void desactivar(Long id) {
        NoticiaEntity noticia = noticiaRepository.findById(id)
            .orElseThrow(() -> new AppException("NEWS_NOT_FOUND", "Noticia no encontrada", 404));

        noticia.setActiva(false);
        noticiaRepository.save(noticia);
        log.info("Noticia desactivada: {}", id);
    }

    private NoticiaDTO mapearADTO(NoticiaEntity noticia) {
        return NoticiaDTO.builder()
            .id(noticia.getId())
            .titulo(noticia.getTitulo())
            .contenido(noticia.getContenido())
            .activa(noticia.getActiva())
            .fechaPublicacion(noticia.getFechaPublicacion())
            .fechaExpiracion(noticia.getFechaExpiracion())
            .autorNombre(noticia.getAutorNombre())
            .numeroLecturas(noticia.getNumeroLecturas())
            .prioridad(noticia.getPrioridad())
            .categoria(noticia.getCategoria())
            .imagenUrl(noticia.getImagenUrl())
            .fechaCreacion(noticia.getFechaCreacion())
            .fechaActualizacion(noticia.getFechaActualizacion())
            .build();
    }
}
