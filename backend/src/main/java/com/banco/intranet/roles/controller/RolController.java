package com.banco.intranet.roles.controller;

import com.banco.intranet.common.dto.ApiResponseDTO;
import com.banco.intranet.roles.service.RolService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<?>> listarRoles(Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(rolService.listarRoles(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<?>> obtenerRol(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(rolService.obtenerRol(id)));
    }

    @GetMapping("/permisos")
    public ResponseEntity<ApiResponseDTO<?>> listarPermisos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(rolService.listarPermisos(pageable)));
    }
}
