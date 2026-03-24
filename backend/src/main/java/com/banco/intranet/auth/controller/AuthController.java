package com.banco.intranet.auth.controller;

import com.banco.intranet.auth.dto.LoginRequestDTO;
import com.banco.intranet.auth.service.AuthService;
import com.banco.intranet.common.dto.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * Controlador de autenticación
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Login de usuario
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<?>> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Solicitud de login para: {}", request.getUsuario());
        
        return ResponseEntity.ok(
            ApiResponseDTO.success(
                authService.login(request),
                "Login exitoso"
            )
        );
    }

    /**
     * Refresca el token JWT
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseDTO<?>> refreshToken(
            @RequestHeader("Authorization") String refreshToken) {
        
        String token = refreshToken.substring(7);
        String nuevoToken = authService.refreshToken(token);
        
        return ResponseEntity.ok(
            ApiResponseDTO.success(
                nuevoToken,
                "Token refrescado exitosamente"
            )
        );
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponseDTO<?>> health() {
        return ResponseEntity.ok(
            ApiResponseDTO.success("OK", "Servicio de autenticación disponible")
        );
    }
}
