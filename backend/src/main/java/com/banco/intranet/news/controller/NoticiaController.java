package com.banco.intranet.news.controller;

import com.banco.intranet.news.dto.NoticiaDTO;
import com.banco.intranet.news.service.NoticiaService;
import com.banco.intranet.common.dto.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * Controlador de noticias
 */
@Slf4j
@RestController
@RequestMapping("/noticias")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NoticiaController {

    @Autowired
    private NoticiaService noticiaService;

    /**
     * Obtiene una noticia por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<?>> obtenerPorId(@PathVariable Long id) {
        log.info("Obteniendo noticia: {}", id);
        noticiaService.incrementarLecturas(id);
        return ResponseEntity.ok(
            ApiResponseDTO.success(noticiaService.obtenerPorId(id))
        );
    }

    /**
     * Obtiene todas las noticias activas
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<?>> obtenerActivas(Pageable pageable) {
        log.info("Obteniendo noticias activas");
        return ResponseEntity.ok(
            ApiResponseDTO.success(noticiaService.obtenerActivas(pageable))
        );
    }

    /**
     * Busca noticias por título
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<?>> buscar(
            @RequestParam String titulo,
            Pageable pageable) {
        log.info("Buscando noticias con título: {}", titulo);
        return ResponseEntity.ok(
            ApiResponseDTO.success(noticiaService.buscar(titulo, pageable))
        );
    }

    /**
     * Crea una nueva noticia
     */
    @PostMapping
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> crear(@Valid @RequestBody NoticiaDTO dto) {
        log.info("Creando nueva noticia: {}", dto.getTitulo());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.success(
                noticiaService.crear(dto, 1L, "SYSTEM"),
                "Noticia creada exitosamente"
            )
        );
    }

    /**
     * Actualiza una noticia
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody NoticiaDTO dto) {
        log.info("Actualizando noticia: {}", id);
        return ResponseEntity.ok(
            ApiResponseDTO.success(
                noticiaService.actualizar(id, dto),
                "Noticia actualizada exitosamente"
            )
        );
    }

    /**
     * Desactiva una noticia
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> desactivar(@PathVariable Long id) {
        log.info("Desactivando noticia: {}", id);
        noticiaService.desactivar(id);
        return ResponseEntity.ok(
            ApiResponseDTO.success(null, "Noticia desactivada exitosamente")
        );
    }
}
