package com.banco.intranet.users.controller;

import com.banco.intranet.users.dto.UsuarioDTO;
import com.banco.intranet.users.service.UsuarioService;
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
 * Controlador de usuarios
 */
@Slf4j
@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene un usuario por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esUsuarioActual(#id)")
    public ResponseEntity<ApiResponseDTO<?>> obtenerPorId(@PathVariable Long id) {
        log.info("Obteniendo usuario: {}", id);
        return ResponseEntity.ok(
            ApiResponseDTO.success(usuarioService.obtenerPorId(id))
        );
    }

    /**
     * Obtiene todos los usuarios
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> obtenerTodos(Pageable pageable) {
        log.info("Obteniendo todos los usuarios");
        return ResponseEntity.ok(
            ApiResponseDTO.success(usuarioService.obtenerTodos(pageable))
        );
    }

    /**
     * Crea un nuevo usuario
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> crear(@Valid @RequestBody UsuarioDTO dto) {
        log.info("Creando nuevo usuario: {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.success(usuarioService.crear(dto), "Usuario creado exitosamente")
        );
    }

    /**
     * Actualiza un usuario
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esUsuarioActual(#id)")
    public ResponseEntity<ApiResponseDTO<?>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO dto) {
        log.info("Actualizando usuario: {}", id);
        return ResponseEntity.ok(
            ApiResponseDTO.success(usuarioService.actualizar(id, dto), "Usuario actualizado exitosamente")
        );
    }

    /**
     * Desactiva un usuario
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<?>> desactivar(@PathVariable Long id) {
        log.info("Desactivando usuario: {}", id);
        usuarioService.desactivar(id);
        return ResponseEntity.ok(
            ApiResponseDTO.success(null, "Usuario desactivado exitosamente")
        );
    }
}
