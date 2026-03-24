package com.banco.intranet.audit.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que registra la auditoría de acciones del sistema
 */
@Entity
@Table(name = "auditoria", indexes = {
    @Index(name = "idx_auditoria_usuario", columnList = "usuario"),
    @Index(name = "idx_auditoria_accion", columnList = "accion"),
    @Index(name = "idx_auditoria_fecha", columnList = "fecha_accion"),
    @Index(name = "idx_auditoria_resultado", columnList = "resultado")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(name = "modulo", nullable = false, length = 100)
    private String modulo;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String detalles;

    @Column(name = "nivel", length = 50)
    private String valuable;

    @Column(nullable = false, length = 20)
    private String resultado;

    @Column(name = "codigo_resultado", length = 10)
    private String codigoResultado;

    @Column(name = "mensaje_error", columnDefinition = "NVARCHAR(MAX)")
    private String mensajeError;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "navegador", length = 200)
    private String navegador;

    @Column(name = "sistema_operativo", length = 100)
    private String sistemaOperativo;

    @Column(name = "id_transaccion", length = 100)
    private String idTransaccion;

    @Column(name = "fecha_accion", nullable = false)
    private LocalDateTime fechaAccion;

    @PrePersist
    protected void onCreate() {
        this.fechaAccion = LocalDateTime.now();
    }
}
