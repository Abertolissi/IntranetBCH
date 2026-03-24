package com.banco.intranet.documents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para Documento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Título es requerido")
    private String titulo;

    private String descripcion;

    @NotBlank(message = "Tipo es requerido")
    private String tipo;

    @NotBlank(message = "Clasificación es requerida")
    private String clasificacion;

    private Boolean activo;

    private Integer numeroDescargas;

    private String autorNombre;

    private String departamento;

    private String etiquetas;

    private String version;

    private LocalDateTime fechaVigenciaInicio;

    private LocalDateTime fechaVigenciaFin;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    private String rutaArchivo;

    private Long tamano;
}
