package com.banco.intranet.documents.controller;

import com.banco.intranet.common.dto.ApiResponseDTO;
import com.banco.intranet.documents.dto.DocumentoDTO;
import com.banco.intranet.documents.service.DocumentoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controlador de documentos.
 */
@Slf4j
@RestController
@RequestMapping("/documentos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<?>> obtenerPorId(@PathVariable Long id) {
        log.info("Obteniendo documento: {}", id);
        return ResponseEntity.ok(ApiResponseDTO.success(documentoService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<?>> obtenerActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(documentoService.obtenerActivos(pageable)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<?>> buscarPorTitulo(@RequestParam String titulo, Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(documentoService.buscarPorTitulo(titulo, pageable)));
    }

    @PostMapping
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> crear(@Valid @RequestBody DocumentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(documentoService.crear(dto), "Documento creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> actualizar(@PathVariable Long id, @Valid @RequestBody DocumentoDTO dto) {
        return ResponseEntity.ok(ApiResponseDTO.success(documentoService.actualizar(id, dto), "Documento actualizado"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> desactivar(@PathVariable Long id) {
        documentoService.desactivar(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Documento desactivado"));
    }

    @PostMapping("/{id}/descargar")
    public ResponseEntity<ApiResponseDTO<?>> registrarDescarga(@PathVariable Long id) {
        documentoService.incrementarDescargas(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Descarga registrada"));
    }
}
