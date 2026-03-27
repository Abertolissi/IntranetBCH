package com.banco.intranet.roles.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Set<String> permisos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
