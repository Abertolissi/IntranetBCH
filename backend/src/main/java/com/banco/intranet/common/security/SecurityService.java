package com.banco.intranet.common.security;

import com.banco.intranet.users.entity.UsuarioEntity;
import com.banco.intranet.users.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Utilidades para evaluaciones de seguridad en expresiones @PreAuthorize.
 */
@Service("securityService")
public class SecurityService {

    private final UsuarioRepository usuarioRepository;

    public SecurityService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean esUsuarioActual(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = String.valueOf(authentication.getPrincipal());
        UsuarioEntity usuarioActual = usuarioRepository.findByEmail(email).orElse(null);
        return usuarioActual != null && usuarioActual.getId().equals(id);
    }
}
