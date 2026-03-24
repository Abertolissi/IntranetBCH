package com.banco.intranet.users.service;

import com.banco.intranet.common.exception.AppException;
import com.banco.intranet.users.dto.UsuarioDTO;
import com.banco.intranet.users.entity.UsuarioEntity;
import com.banco.intranet.users.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de usuarios
 */
@Slf4j
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene un usuario por ID
     */
    public UsuarioDTO obtenerPorId(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new AppException("USER_NOT_FOUND", "Usuario no encontrado", 404));
        return mapearADTO(usuario);
    }

    /**
     * Obtiene todos los usuarios
     */
    public Page<UsuarioDTO> obtenerTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
            .map(this::mapearADTO);
    }

    /**
     * Crea un nuevo usuario
     */
    public UsuarioDTO crear(UsuarioDTO dto) {
        log.info("Creando nuevo usuario: {}", dto.getEmail());

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new AppException("USER_DUPLICATE_EMAIL", "Email ya existe", 409);
        }

        if (usuarioRepository.existsByNumeroEmpleado(dto.getNumeroEmpleado())) {
            throw new AppException("USER_DUPLICATE_EMPLOYEE", "Número de empleado ya existe", 409);
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setNumeroEmpleado(dto.getNumeroEmpleado());
        usuario.setDepartamento(dto.getDepartamento());
        usuario.setPuesto(dto.getPuesto());
        usuario.setActivo(true);
        usuario.setContrasena(passwordEncoder.encode("Password123!"));
        usuario.setUsuarioCreacion("SYSTEM");

        UsuarioEntity guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente: {}", guardado.getId());

        return mapearADTO(guardado);
    }

    /**
     * Actualiza un usuario
     */
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        log.info("Actualizando usuario: {}", id);

        UsuarioEntity usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new AppException("USER_NOT_FOUND", "Usuario no encontrado", 404));

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setDepartamento(dto.getDepartamento());
        usuario.setPuesto(dto.getPuesto());
        usuario.setActivo(dto.getActivo());
        usuario.setUsuarioActualizacion("SYSTEM");

        UsuarioEntity actualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado: {}", id);

        return mapearADTO(actualizado);
    }

    /**
     * Cambia la contraseña de un usuario
     */
    public void cambiarContrasena(Long usuarioId, String contrasenaActual, String contrasenaNueva) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new AppException("USER_NOT_FOUND", "Usuario no encontrado", 404));

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new AppException("INVALID_PASSWORD", "Contraseña actual incorrecta", 400);
        }

        usuario.setContrasena(passwordEncoder.encode(contrasenaNueva));
        usuario.setFechaCambioContrasena(LocalDateTime.now());
        usuarioRepository.save(usuario);

        log.info("Contraseña cambiada para usuario: {}", usuarioId);
    }

    /**
     * Desactiva un usuario
     */
    public void desactivar(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new AppException("USER_NOT_FOUND", "Usuario no encontrado", 404));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuario desactivado: {}", id);
    }

    private UsuarioDTO mapearADTO(UsuarioEntity usuario) {
        return UsuarioDTO.builder()
            .id(usuario.getId())
            .nombre(usuario.getNombre())
            .apellido(usuario.getApellido())
            .email(usuario.getEmail())
            .numeroEmpleado(usuario.getNumeroEmpleado())
            .departamento(usuario.getDepartamento())
            .puesto(usuario.getPuesto())
            .activo(usuario.getActivo())
            .cuentaBloqueada(usuario.getCuentaBloqueada())
            .roles(usuario.getRoles().stream()
                .map(r -> r.getNombre())
                .collect(Collectors.toSet()))
            .fechaUltimoLogin(usuario.getFechaUltimoLogin())
            .fechaCreacion(usuario.getFechaCreacion())
            .fechaActualizacion(usuario.getFechaActualizacion())
            .build();
    }
}
