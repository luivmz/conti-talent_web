package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import java.time.LocalDateTime;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EvaluacionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.HistorialEstadoPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IHistorialEstadoPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPreguntaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.EvaluacionCompuestaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEvaluacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Logica de evaluacion tecnica:
 *  - Evita doble evaluacion.
 *  - Calcula puntaje sobre 100.
 *  - Mueve estado: APROBADO_TECNICO si >= 70, sino EN_EVALUACION.
 */
@Service
public class EvaluacionServiceImpl implements IEvaluacionService {

    private static final int UMBRAL_APROBACION = 70;

    private final IPostulanteRepository postulanteRepository;
    private final IPreguntaRepository preguntaRepository;
    private final IEstadoRepository estadoRepository;
    private final IHistorialEstadoPostulanteRepository historialRepository;
    private final EvaluacionCompuestaService evaluacionCompuestaService;

    public EvaluacionServiceImpl(IPostulanteRepository postulanteRepository,
                                 IPreguntaRepository preguntaRepository,
                                 IEstadoRepository estadoRepository,
                                 IHistorialEstadoPostulanteRepository historialRepository,
                                 EvaluacionCompuestaService evaluacionCompuestaService) {
        this.postulanteRepository = postulanteRepository;
        this.preguntaRepository = preguntaRepository;
        this.estadoRepository = estadoRepository;
        this.historialRepository = historialRepository;
        this.evaluacionCompuestaService = evaluacionCompuestaService;
    }

    @Override
    @Transactional
    public EvaluacionDTO calificar(EvaluacionRequest request) {
        validarRequest(request);
        Postulante postulante = buscarPostulanteOFallar(request.getPostulanteId());

        verificarQueNoEsteYaEvaluado(postulante);
        verificarQueEsteEnEtapaQuePermiteEvaluar(postulante);

        List<Pregunta> preguntasOferta = preguntaRepository.findByOfertaId(postulante.getOfertaId());
        Map<Long, Integer> respuestasMarcadas = request.getRespuestas() != null
                ? request.getRespuestas()
                : new HashMap<>();

        int totalPreguntas      = preguntasOferta.size();
        int respuestasCorrectas = contarRespuestasCorrectas(preguntasOferta, respuestasMarcadas);
        int puntajeFinal        = calcularPuntajeSobre100(respuestasCorrectas, totalPreguntas);

        actualizarPostulanteConEvaluacion(postulante, puntajeFinal, respuestasMarcadas);

        return new EvaluacionDTO(
                postulante.getId(), postulante.getOfertaId(),
                puntajeFinal, respuestasCorrectas, totalPreguntas,
                respuestasMarcadas, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluacionDTO obtenerPorPostulante(Long postulanteId) {
        Postulante postulante = buscarPostulanteOFallar(postulanteId);
        if (postulante.getRespuestas() == null || postulante.getRespuestas().isEmpty()) {
            throw new ResourceNotFoundException(
                    "El postulante no ha realizado una evaluacion todavia");
        }
        List<Pregunta> preguntasOferta = preguntaRepository.findByOfertaId(postulante.getOfertaId());
        int respuestasCorrectas = contarRespuestasCorrectas(preguntasOferta, postulante.getRespuestas());

        return new EvaluacionDTO(
                postulante.getId(), postulante.getOfertaId(), postulante.getPuntaje(),
                respuestasCorrectas, preguntasOferta.size(),
                postulante.getRespuestas(), postulante.getFechaEvaluacion());
    }

    /* ============ Helpers privados ============ */

    private static void validarRequest(EvaluacionRequest request) {
        if (request == null || request.getPostulanteId() == null) {
            throw new BusinessException("postulanteId requerido");
        }
    }

    private Postulante buscarPostulanteOFallar(Long id) {
        return postulanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulante no encontrado: " + id));
    }

    private void verificarQueNoEsteYaEvaluado(Postulante postulante) {
        if (postulante.getRespuestas() != null && !postulante.getRespuestas().isEmpty()) {
            throw new BusinessException("El postulante ya tiene una evaluacion registrada");
        }
    }

    private void verificarQueEsteEnEtapaQuePermiteEvaluar(Postulante postulante) {
        Estado estadoActual = postulante.getEstado();
        EstadoCodigo codigoActual = EstadoCodigo.valueOf(estadoActual.getCodigo());
        if (codigoActual != EstadoCodigo.POSTULADO && codigoActual != EstadoCodigo.EN_EVALUACION) {
            throw new BusinessException(
                    "El postulante no esta en una etapa que permita evaluar (estado actual: "
                            + codigoActual + ")");
        }
    }

    private static int contarRespuestasCorrectas(List<Pregunta> preguntas,
                                                 Map<Long, Integer> respuestas) {
        int correctas = 0;
        for (Pregunta pregunta : preguntas) {
            Integer indiceMarcado = respuestas.get(pregunta.getId());
            if (indiceMarcado != null && indiceMarcado == pregunta.getCorrecta()) {
                correctas++;
            }
        }
        return correctas;
    }

    private static int calcularPuntajeSobre100(int correctas, int total) {
        if (total == 0) return 0;
        return (int) Math.round((correctas * 100.0) / total);
    }

    private void actualizarPostulanteConEvaluacion(Postulante postulante,
                                                   int puntajeFinal,
                                                   Map<Long, Integer> respuestas) {
        postulante.setPuntaje(puntajeFinal);
        postulante.setRespuestas(new HashMap<>(respuestas));
        postulante.setFechaEvaluacion(LocalDateTime.now());
        evaluacionCompuestaService.recalcular(postulante);

        EstadoCodigo codigoSiguiente = puntajeFinal >= UMBRAL_APROBACION
                ? EstadoCodigo.APROBADO_TECNICO
                : EstadoCodigo.EN_EVALUACION;
        String codigoAnterior = postulante.getEstado() != null ? postulante.getEstado().getCodigo() : null;
        Estado estadoSiguiente = estadoRepository.findByCodigo(codigoSiguiente.name())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estado '" + codigoSiguiente + "' no esta cargado en el sistema"));
        postulante.setEstado(estadoSiguiente);

        Postulante guardado = postulanteRepository.save(postulante);
        if (codigoAnterior == null || !codigoAnterior.equals(codigoSiguiente.name())) {
            HistorialEstadoPostulante historial = new HistorialEstadoPostulante();
            historial.setPostulante(guardado);
            historial.setEstadoAnterior(codigoAnterior);
            historial.setEstadoNuevo(codigoSiguiente.name());
            historial.setFechaCambio(LocalDateTime.now());
            historial.setUsuarioAdmin("Sistema");
            historial.setObservacionInterna("Evaluacion tecnica registrada");
            historial.setObservacionPostulante("Tu evaluacion tecnica fue registrada.");
            historialRepository.save(historial);
        }
    }
}
