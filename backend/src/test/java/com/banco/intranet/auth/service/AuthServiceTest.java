package com.banco.intranet.auth.service;

import com.banco.intranet.auth.dto.LoginRequestDTO;
import com.banco.intranet.common.exception.AppException;
import com.banco.intranet.common.util.JwtTokenProvider;
import com.banco.intranet.roles.entity.RolEntity;
import com.banco.intranet.users.entity.UsuarioEntity;
import com.banco.intranet.users.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UsuarioEntity usuario;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setNombre("Admin");
        usuario.setApellido("Sistema");
        usuario.setEmail("admin@banco.local");
        usuario.setNumeroEmpleado("EMP001");
        usuario.setContrasena("hashed-password");
        usuario.setActivo(true);
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);

        RolEntity rol = new RolEntity();
        rol.setNombre("ADMIN");
        Set<RolEntity> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);
    }

    @Test
    void login_exitoso_debe_generar_tokens_y_resetear_intentos() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .usuario("admin@banco.local")
                .contrasena("AdminPassword123!")
                .build();

        when(usuarioRepository.findByEmailOrNumeroEmpleado("admin@banco.local", "admin@banco.local"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("AdminPassword123!", "hashed-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString(), anyString(), anyString(), anyCollection())).thenReturn("jwt-token");
        when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("refresh-token");

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(0, usuario.getIntentosFallidos());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void login_invalido_con_quinto_intento_debe_bloquear_cuenta() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .usuario("admin@banco.local")
                .contrasena("wrong")
                .build();

        usuario.setIntentosFallidos(4);

        when(usuarioRepository.findByEmailOrNumeroEmpleado("admin@banco.local", "admin@banco.local"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> authService.login(request));

        assertEquals("BAD_CREDENTIALS", ex.getErrorCode());
        assertEquals(true, usuario.getCuentaBloqueada());
        assertEquals(5, usuario.getIntentosFallidos());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void refresh_token_invalido_debe_lanzar_excepcion() {
        when(jwtTokenProvider.validateToken("bad-token")).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> authService.refreshToken("bad-token"));

        assertEquals("INVALID_REFRESH_TOKEN", ex.getErrorCode());
        assertEquals(401, ex.getHttpStatus());
    }
}
