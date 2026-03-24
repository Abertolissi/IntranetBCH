package com.banco.intranet.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para Usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Nombre es requerido")
    private String nombre;

    @NotBlank(message = "Apellido es requerido")
    private String apellido;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    @NotBlank(message = "Número de empleado es requerido")
    private String numeroEmpleado;

    private String departamento;

    private String puesto;

    private Boolean activo;

    private Boolean cuentaBloqueada;

    private LocalDateTime fechaUltimoLogin;

    private Set<String> roles;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;
}
