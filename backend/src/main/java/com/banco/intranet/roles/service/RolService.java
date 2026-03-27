package com.banco.intranet.roles.service;

import com.banco.intranet.common.exception.AppException;
import com.banco.intranet.roles.dto.PermisoDTO;
import com.banco.intranet.roles.dto.RolDTO;
import com.banco.intranet.roles.entity.PermisoEntity;
import com.banco.intranet.roles.entity.RolEntity;
import com.banco.intranet.roles.repository.PermisoRepository;
import com.banco.intranet.roles.repository.RolRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    public RolService(RolRepository rolRepository, PermisoRepository permisoRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    public Page<RolDTO> listarRoles(Pageable pageable) {
        return rolRepository.findAll(pageable).map(this::mapearRol);
    }

    public RolDTO obtenerRol(Long id) {
        RolEntity rol = rolRepository.findById(id)
                .orElseThrow(() -> new AppException("ROLE_NOT_FOUND", "Rol no encontrado", 404));
        return mapearRol(rol);
    }

    public Page<PermisoDTO> listarPermisos(Pageable pageable) {
        return permisoRepository.findAll(pageable).map(this::mapearPermiso);
    }

    private RolDTO mapearRol(RolEntity rol) {
        return RolDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.getActivo())
                .permisos(rol.getPermisos().stream().map(PermisoEntity::getCodigo).collect(Collectors.toSet()))
                .fechaCreacion(rol.getFechaCreacion())
                .fechaActualizacion(rol.getFechaActualizacion())
                .build();
    }

    private PermisoDTO mapearPermiso(PermisoEntity permiso) {
        return PermisoDTO.builder()
                .id(permiso.getId())
                .codigo(permiso.getCodigo())
                .nombre(permiso.getNombre())
                .descripcion(permiso.getDescripcion())
                .modulo(permiso.getModulo())
                .activo(permiso.getActivo())
                .build();
    }
}
