package com.banco.intranet.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para Noticia
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticiaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Título es requerido")
    private String titulo;

    @NotBlank(message = "Contenido es requerido")
    private String contenido;

    private Boolean activa;

    private LocalDateTime fechaPublicacion;

    private LocalDateTime fechaExpiracion;

    private String autorNombre;

    private Integer numeroLecturas;

    private String prioridad;

    private String categoria;

    private String imagenUrl;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;
}
