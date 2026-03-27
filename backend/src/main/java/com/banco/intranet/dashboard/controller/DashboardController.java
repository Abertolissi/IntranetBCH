package com.banco.intranet.dashboard.controller;

import com.banco.intranet.common.dto.ApiResponseDTO;
import com.banco.intranet.documents.repository.DocumentoRepository;
import com.banco.intranet.news.repository.NoticiaRepository;
import com.banco.intranet.users.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de dashboard
 */
@Slf4j
@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    private final UsuarioRepository usuarioRepository;
    private final NoticiaRepository noticiaRepository;
    private final DocumentoRepository documentoRepository;

    public DashboardController(
            UsuarioRepository usuarioRepository,
            NoticiaRepository noticiaRepository,
            DocumentoRepository documentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.noticiaRepository = noticiaRepository;
        this.documentoRepository = documentoRepository;
    }

    /**
     * Obtiene datos del dashboard
     */
    @GetMapping("/datos")
    public ResponseEntity<ApiResponseDTO<?>> obtenerDatos() {
        log.info("Obteniendo datos del dashboard");
        
        long totalUsuarios = usuarioRepository.count();
        long usuariosActivos = usuarioRepository.countByActivoTrue();
        long noticiasActivas = noticiaRepository.countByActivaTrue();
        long documentosActivos = documentoRepository.countByActivoTrue();

        Map<String, Object> datos = new HashMap<>();
        datos.put("totalUsuarios", totalUsuarios);
        datos.put("usuariosActivos", usuariosActivos);
        datos.put("noticiasRecientes", noticiasActivas);
        datos.put("documentosDisponibles", documentosActivos);
        
        return ResponseEntity.ok(
            ApiResponseDTO.success(datos, "Datos del dashboard cargados")
        );
    }

    /**
     * Obtiene widgets configurables
     */
    @GetMapping("/widgets")
    public ResponseEntity<ApiResponseDTO<?>> obtenerWidgets() {
        log.info("Obteniendo widgets del dashboard");
        
        Map<String, Object> widgets = new HashMap<>();
        widgets.put("widget1", "Últimas Noticias");
        widgets.put("widget2", "Documentos Destacados");
        widgets.put("widget3", "Estadísticas");
        
        return ResponseEntity.ok(
            ApiResponseDTO.success(widgets, "Widgets cargados")
        );
    }
}
