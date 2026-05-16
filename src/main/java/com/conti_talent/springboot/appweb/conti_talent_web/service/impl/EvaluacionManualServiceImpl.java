package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import java.time.LocalDateTime;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EntrevistaRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionPsicologicaRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.model.EntrevistaPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.EvaluacionPsicologicaPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEntrevistaPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEvaluacionPsicologicaPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEvaluacionManualService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluacionManualServiceImpl implements IEvaluacionManualService {

    private final IPostulanteRepository postulanteRepository;
    private final IEntrevistaPostulanteRepository entrevistaRepository;
    private final IEvaluacionPsicologicaPostulanteRepository psicologicaRepository;
    private final IPostulanteService postulanteService;

    public EvaluacionManualServiceImpl(IPostulanteRepository postulanteRepository,
                                       IEntrevistaPostulanteRepository entrevistaRepository,
                                       IEvaluacionPsicologicaPostulanteRepository psicologicaRepository,
                                       IPostulanteService postulanteService) {
        this.postulanteRepository = postulanteRepository;
        this.entrevistaRepository = entrevistaRepository;
        this.psicologicaRepository = psicologicaRepository;
        this.postulanteService = postulanteService;
    }

    @Override
    @Transactional
    public PostulanteDTO registrarEntrevista(Long postulanteId, EntrevistaRequest request) {
        if (request == null) throw new BusinessException("Datos de entrevista requeridos");
        Postulante postulante = postulanteRepository.findById(postulanteId)
                .orElseThrow(() -> new BusinessException("Postulante inexistente: " + postulanteId));

        EntrevistaPostulante entrevista = new EntrevistaPostulante();
        entrevista.setPostulante(postulante);
        entrevista.setTipoEntrevista("ENTREVISTA_NORMAL");
        entrevista.setFechaEntrevista(request.getFechaEntrevista() != null ? request.getFechaEntrevista() : LocalDateTime.now());
        entrevista.setModalidad("VIRTUAL");
        entrevista.setEstadoEntrevista("PROGRAMADA");
        entrevista.setResultado(texto(request.getResultado(), "PENDIENTE"));
        entrevista.setObservacion(request.getObservacion());
        entrevista.setUsuarioAdmin(texto(request.getUsuarioAdmin(), "Admin"));
        entrevista.setCreadoEn(LocalDateTime.now());
        entrevistaRepository.save(entrevista);

        String resultado = entrevista.getResultado().toUpperCase();
        if ("APTO".equals(resultado)) {
            if (postulante.getEstado() != null && EstadoCodigo.APROBADO_TECNICO.name().equals(postulante.getEstado().getCodigo())) {
                postulanteService.cambiarEstado(postulanteId, EstadoCodigo.ENTREVISTA.name(),
                        entrevista.getUsuarioAdmin(), "Entrevista iniciada", "Tu entrevista fue registrada.");
            }
            return postulanteService.cambiarEstado(postulanteId, EstadoCodigo.EVALUACION_PSICOLOGICA.name(),
                    entrevista.getUsuarioAdmin(), request.getObservacion(), request.getObservacionPostulante());
        }
        if ("NO_APTO".equals(resultado) || "RECHAZADO".equals(resultado)) {
            return postulanteService.cambiarEstado(postulanteId, EstadoCodigo.RECHAZADO.name(),
                    entrevista.getUsuarioAdmin(), request.getObservacion(), request.getObservacionPostulante());
        }
        if (postulante.getEstado() != null && EstadoCodigo.APROBADO_TECNICO.name().equals(postulante.getEstado().getCodigo())) {
            return postulanteService.cambiarEstado(postulanteId, EstadoCodigo.ENTREVISTA.name(),
                    entrevista.getUsuarioAdmin(), "Entrevista registrada", request.getObservacionPostulante());
        }
        return postulanteService.obtenerPorId(postulanteId);
    }

    @Override
    @Transactional
    public PostulanteDTO registrarEvaluacionPsicologica(Long postulanteId, EvaluacionPsicologicaRequest request) {
        if (request == null) throw new BusinessException("Datos de evaluacion psicologica requeridos");
        Postulante postulante = postulanteRepository.findById(postulanteId)
                .orElseThrow(() -> new BusinessException("Postulante inexistente: " + postulanteId));

        EvaluacionPsicologicaPostulante evaluacion = new EvaluacionPsicologicaPostulante();
        evaluacion.setPostulante(postulante);
        evaluacion.setFechaEvaluacion(request.getFechaEvaluacion() != null ? request.getFechaEvaluacion() : LocalDateTime.now());
        evaluacion.setResultado(texto(request.getResultado(), "PENDIENTE"));
        evaluacion.setObservacion(request.getObservacion());
        evaluacion.setUsuarioAdmin(texto(request.getUsuarioAdmin(), "Admin"));
        evaluacion.setCreadoEn(LocalDateTime.now());
        psicologicaRepository.save(evaluacion);

        String resultado = evaluacion.getResultado().toUpperCase();
        if ("APTO".equals(resultado)) {
            return postulanteService.cambiarEstado(postulanteId, EstadoCodigo.ACEPTADO.name(),
                    evaluacion.getUsuarioAdmin(), request.getObservacion(), request.getObservacionPostulante());
        }
        if ("NO_APTO".equals(resultado) || "RECHAZADO".equals(resultado)) {
            return postulanteService.cambiarEstado(postulanteId, EstadoCodigo.RECHAZADO.name(),
                    evaluacion.getUsuarioAdmin(), request.getObservacion(), request.getObservacionPostulante());
        }
        return postulanteService.obtenerPorId(postulanteId);
    }

    private String texto(String valor, String fallback) {
        return valor == null || valor.trim().isEmpty() ? fallback : valor.trim();
    }
}
