package com.banco.intranet.auth.service;

import com.banco.intranet.auth.dto.LoginRequestDTO;
import com.banco.intranet.auth.dto.LoginResponseDTO;
import com.banco.intranet.common.exception.AppException;
import com.banco.intranet.common.util.JwtTokenProvider;
import com.banco.intranet.users.entity.UsuarioEntity;
import com.banco.intranet.users.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Servicio de autenticación
 */
@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Realiza login del usuario
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Intento de login para usuario: {}", request.getUsuario());

        UsuarioEntity usuario = usuarioRepository.findByEmailOrNumeroEmpleado(request.getUsuario(), request.getUsuario())
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado: {}", request.getUsuario());
                return new AppException("USER_NOT_FOUND", "Usuario no encontrado", 401);
            });

        if (!usuario.getActivo()) {
            log.warn("Intento de login con usuario inactivo: {}", usuario.getEmail());
            throw new AppException("USER_INACTIVE", "Usuario inactivo", 403);
        }

        if (usuario.getCuentaBloqueada()) {
            log.warn("Intento de login con cuenta bloqueada: {}", usuario.getEmail());
            throw new AppException("ACCOUNT_LOCKED", "Cuenta bloqueada", 403);
        }

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            log.warn("Contraseña incorrecta para usuario: {}", usuario.getEmail());
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            
            // Bloquear cuenta después de 5 intentos fallidos
            if (usuario.getIntentosFallidos() >= 5) {
                usuario.setCuentaBloqueada(true);
                log.warn("Cuenta bloqueada por múltiples intentos: {}", usuario.getEmail());
            }
            
            usuarioRepository.save(usuario);
            throw new AppException("BAD_CREDENTIALS", "Usuario o contraseña incorrectos", 401);
        }

        // Autenticación exitosa
        usuario.setIntentosFallidos(0);
        usuario.setFechaUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getEmail(), usuario.getId().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario.getEmail());

        log.info("Login exitoso para usuario: {}", usuario.getEmail());

        return LoginResponseDTO.builder()
            .token(token)
            .refreshToken(refreshToken)
            .noNombreUsuario(usuario.getNumeroEmpleado())
            .email(usuario.getEmail())
            .nombreCompleto(usuario.getNombreCompleto())
            .usuarioId(usuario.getId())
            .build();
    }

    /**
     * Refresca el token JWT
     */
    public String refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AppException("INVALID_REFRESH_TOKEN", "Token de refresco inválido", 401);
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        UsuarioEntity usuario = usuarioRepository.findByEmail(username)
            .orElseThrow(() -> new AppException("USER_NOT_FOUND", "Usuario no encontrado", 404));

        return jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getEmail(), usuario.getId().toString());
    }

    /**
     * Valida que el usuario sea un usuario válido
     */
    public UsuarioEntity obtenerUsuarioActual(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new AppException("USER_NOT_FOUND", "Usuario no encontrado", 404));
    }
}
