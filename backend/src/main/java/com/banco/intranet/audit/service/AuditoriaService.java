package com.banco.intranet.audit.service;

import com.banco.intranet.audit.entity.AuditoriaEntity;
import com.banco.intranet.audit.repository.AuditoriaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio de auditoría
 */
@Slf4j
@Service
@Transactional
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    /**
     * Registra una acción de auditoría
     */
    public void registrarAccion(Long usuarioId, String usuario, String accion, String modulo, 
                               String detalles, String resultado, String codigoResultado,
                               String direccionIp, String idTransaccion) {
        try {
            AuditoriaEntity auditoria = AuditoriaEntity.builder()
                .usuarioId(usuarioId)
                .usuario(usuario)
                .accion(accion)
                .modulo(modulo)
                .detalles(detalles)
                .resultado(resultado)
                .codigoResultado(codigoResultado)
                .direccionIp(direccionIp)
                .idTransaccion(idTransaccion)
                .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception ex) {
            log.error("Error registrando auditoría: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Obtiene auditoría por usuario
     */
    public Page<AuditoriaEntity> obtenerPorUsuario(Long usuarioId, Pageable pageable) {
        return auditoriaRepository.findByUsuarioId(usuarioId, pageable);
    }

    /**
     * Obtiene auditoría por acción
     */
    public Page<AuditoriaEntity> obtenerPorAccion(String accion, Pageable pageable) {
        return auditoriaRepository.findByAccion(accion, pageable);
    }

    /**
     * Obtiene auditoría por módulo
     */
    public Page<AuditoriaEntity> obtenerPorModulo(String modulo, Pageable pageable) {
        return auditoriaRepository.findByModulo(modulo, pageable);
    }

    /**
     * Obtiene auditoría por rango de fechas
     */
    public Page<AuditoriaEntity> obtenerPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        return auditoriaRepository.findByFechaAccionBetween(fechaInicio, fechaFin, pageable);
    }
}
