package com.banco.intranet.audit.controller;

import com.banco.intranet.audit.service.AuditoriaService;
import com.banco.intranet.common.dto.ApiResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<?>> listar(Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(auditoriaService.obtenerPorFechas(
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                pageable
        )));
    }

    @GetMapping("/usuario")
    public ResponseEntity<ApiResponseDTO<?>> porUsuario(@RequestParam Long usuarioId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(auditoriaService.obtenerPorUsuario(usuarioId, pageable)));
    }

    @GetMapping("/accion")
    public ResponseEntity<ApiResponseDTO<?>> porAccion(@RequestParam String accion, Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(auditoriaService.obtenerPorAccion(accion, pageable)));
    }

    @GetMapping("/modulo")
    public ResponseEntity<ApiResponseDTO<?>> porModulo(@RequestParam String modulo, Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(auditoriaService.obtenerPorModulo(modulo, pageable)));
    }

    @GetMapping("/fechas")
    public ResponseEntity<ApiResponseDTO<?>> porFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(auditoriaService.obtenerPorFechas(inicio, fin, pageable)));
    }
}
