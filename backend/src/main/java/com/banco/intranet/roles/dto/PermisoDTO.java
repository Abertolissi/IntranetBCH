package com.banco.intranet.roles.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String modulo;
    private Boolean activo;
}
