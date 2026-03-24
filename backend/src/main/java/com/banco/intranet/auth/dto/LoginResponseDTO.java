package com.banco.intranet.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * DTO para respuesta de login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private String refreshToken;
    private String noNombreUsuario;
    private String email;
    private String nombreCompleto;
    private Long usuarioId;
}
