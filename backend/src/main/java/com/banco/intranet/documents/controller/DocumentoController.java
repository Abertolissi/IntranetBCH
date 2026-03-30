package com.banco.intranet.documents.controller;

import com.banco.intranet.common.dto.ApiResponseDTO;
import com.banco.intranet.common.exception.AppException;
import com.banco.intranet.documents.dto.DocumentoDTO;
import com.banco.intranet.documents.service.DocumentoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;

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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> subirArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("titulo") String titulo,
            @RequestParam("clasificacion") String clasificacion,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "departamento", required = false) String departamento,
            @RequestParam(value = "etiquetas", required = false) String etiquetas,
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "autorNombre", required = false) String autorNombre) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new AppException("DOC_TITLE_REQUIRED", "El título es obligatorio", 400);
        }

        if (clasificacion == null || clasificacion.trim().isEmpty()) {
            throw new AppException("DOC_CLASS_REQUIRED", "La clasificación es obligatoria", 400);
        }

        DocumentoDTO dto = DocumentoDTO.builder()
                .titulo(titulo.trim())
                .descripcion(descripcion)
                .clasificacion(clasificacion.trim())
                .departamento(departamento)
                .etiquetas(etiquetas)
                .version(version)
                .autorNombre(autorNombre)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(documentoService.crearConArchivo(dto, archivo), "Documento cargado exitosamente"));
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

    @GetMapping("/{id}/archivo")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable Long id) {
        Resource resource = documentoService.obtenerArchivoParaDescarga(id);
        String nombreDescarga = documentoService.construirNombreDescarga(id);
        String tipoMime = documentoService.obtenerTipoMime(id);

        documentoService.incrementarDescargas(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(tipoMime))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(nombreDescarga, StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(resource);
    }
}
