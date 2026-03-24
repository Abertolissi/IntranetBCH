package com.banco.intranet.documents.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que representa un documento del sistema
 */
@Entity
@Table(name = "documentos", indexes = {
    @Index(name = "idx_documento_tipo", columnList = "tipo"),
    @Index(name = "idx_documento_clasificacion", columnList = "clasificacion"),
    @Index(name = "idx_documento_activo", columnList = "activo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 500)
    private String rutaArchivo;

    @Column(nullable = false, length = 20)
    private String extension;

    @Column(nullable = false)
    private Long tamanio;

    @Column(nullable = false, length = 50)
    private String clasificacion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "numero_descargas")
    @Builder.Default
    private Integer numeroDescargas = 0;

    @Column(name = "autor_id")
    private Long autorId;

    @Column(name = "autor_nombre", length = 100)
    private String autorNombre;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Column(name = "etiquetas", length = 500)
    private String etiquetas;

    @Column(name = "version", length = 20)
    @Builder.Default
    private String version = "1.0";

    @Column(name = "fecha_vigencia_inicio")
    private LocalDateTime fechaVigenciaInicio;

    @Column(name = "fecha_vigencia_fin")
    private LocalDateTime fechaVigenciaFin;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_creacion", nullable = false, updatable = false, length = 50)
    private String usuarioCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
