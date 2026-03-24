package com.banco.intranet.news.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que representa una noticia interna
 */
@Entity
@Table(name = "noticias", indexes = {
    @Index(name = "idx_noticia_fecha_publicacion", columnList = "fecha_publicacion"),
    @Index(name = "idx_noticia_activa", columnList = "activa")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String contenido;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "autor_id")
    private Long autorId;

    @Column(name = "autor_nombre", length = 100)
    private String autorNombre;

    @Column(name = "numero_lecturas")
    @Builder.Default
    private Integer numeroLecturas = 0;

    @Column(name = "prioridad", length = 20)
    @Builder.Default
    private String prioridad = "NORMAL";

    @Column(name = "categoria", length = 50)
    private String categoria;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

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
        if (this.fechaPublicacion == null) {
            this.fechaPublicacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
