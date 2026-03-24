package com.banco.intranet.dashboard.controller;

import com.banco.intranet.common.dto.ApiResponseDTO;
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

    /**
     * Obtiene datos del dashboard
     */
    @GetMapping("/datos")
    public ResponseEntity<ApiResponseDTO<?>> obtenerDatos() {
        log.info("Obteniendo datos del dashboard");
        
        Map<String, Object> datos = new HashMap<>();
        datos.put("totalUsuarios", 150);
        datos.put("usuariosActivos", 145);
        datos.put("noticiasRecientes", 12);
        datos.put("documentosDisponibles", 287);
        
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
