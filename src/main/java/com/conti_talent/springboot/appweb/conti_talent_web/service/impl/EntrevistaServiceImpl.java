package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import java.time.LocalDateTime;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EntrevistaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EntrevistaRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.EntrevistaMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.EntrevistaPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEntrevistaPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IUsuarioRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEntrevistaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
@Service
public class EntrevistaServiceImpl implements IEntrevistaService {

    private static final String NORMAL = "ENTREVISTA_NORMAL";
    private static final String PSICOLOGICA = "EVALUACION_PSICOLOGICA";
    private static final List<String> ACTIVAS = List.of("PROGRAMADA", "REPROGRAMADA", "REALIZADA");

    private final IEntrevistaPostulanteRepository entrevistaRepository;
    private final IPostulanteRepository postulanteRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IPostulanteService postulanteService;
    private final EntrevistaMapper entrevistaMapper;

    public EntrevistaServiceImpl(IEntrevistaPostulanteRepository entrevistaRepository,
                                 IPostulanteRepository postulanteRepository,
                                 IUsuarioRepository usuarioRepository,
                                 IPostulanteService postulanteService,
                                 EntrevistaMapper entrevistaMapper) {
        this.entrevistaRepository = entrevistaRepository;
        this.postulanteRepository = postulanteRepository;
        this.usuarioRepository = usuarioRepository;
        this.postulanteService = postulanteService;
        this.entrevistaMapper = entrevistaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntrevistaDTO> listarPorPostulante(Long postulanteId) {
        return entrevistaMapper.convertirALista(entrevistaRepository.findByPostulante_IdOrderByFechaProgramadaDesc(postulanteId));
    }

    @Override
    @Transactional
    public EntrevistaDTO crear(Long postulanteId, EntrevistaRequest request) {
        if (request == null) throw new BusinessException("Datos de entrevista requeridos");
        Postulante postulante = buscarPostulante(postulanteId);
        String tipo = normalizar(request.getTipoEntrevista(), NORMAL);
        validarCreacionPorEstado(postulante, tipo);
        validarNoDuplicada(postulanteId, tipo);

        EntrevistaPostulante entrevista = new EntrevistaPostulante();
        entrevista.setPostulante(postulante);
        entrevista.setTipoEntrevista(tipo);
        entrevista.setEstadoEntrevista("PROGRAMADA");
        entrevista.setResultado("PENDIENTE");
        copiarDatosProgramacion(entrevista, request);
        entrevista.setObservacionInterna(texto(request.getObservacionInterna(), request.getObservacion()));
        entrevista.setObservacionPostulante(limpiar(request.getObservacionPostulante()));
        entrevista.setUsuarioAdmin(texto(request.getUsuarioAdmin(), "Admin"));
        entrevista.setCreadoPorAdmin(buscarAdmin(request.getUsuarioAdmin()));
        entrevista.setCreadoEn(LocalDateTime.now());
        EntrevistaPostulante guardada = entrevistaRepository.save(entrevista);

        if (NORMAL.equals(tipo) && EstadoCodigo.APROBADO_TECNICO.name().equals(codigoEstado(postulante))) {
            postulanteService.cambiarEstado(postulanteId, EstadoCodigo.ENTREVISTA.name(), entrevista.getUsuarioAdmin(),
                    "Entrevista programada", mensajeVisible(entrevista));
        }
        return entrevistaMapper.convertirADTO(guardada);
    }

    @Override
    @Transactional
    public EntrevistaDTO actualizar(Long entrevistaId, EntrevistaRequest request) {
        if (request == null) throw new BusinessException("Datos de entrevista requeridos");
        EntrevistaPostulante entrevista = buscarEntrevista(entrevistaId);
        boolean terminal = esTerminal(entrevista.getPostulante());
        if (!terminal) {
            copiarDatosProgramacion(entrevista, request);
            if (!esVacio(request.getEstadoEntrevista())) entrevista.setEstadoEntrevista(normalizar(request.getEstadoEntrevista(), entrevista.getEstadoEntrevista()));
            if (!esVacio(request.getObservacionPostulante())) entrevista.setObservacionPostulante(request.getObservacionPostulante().trim());
        }
        entrevista.setObservacionInterna(texto(request.getObservacionInterna(), request.getObservacion()));
        registrarAuditoria(entrevista, request);
        return entrevistaMapper.convertirADTO(entrevistaRepository.save(entrevista));
    }

    @Override
    @Transactional
    public PostulanteDTO registrarResultado(Long entrevistaId, EntrevistaRequest request) {
        if (request == null) throw new BusinessException("Datos de resultado requeridos");
        EntrevistaPostulante entrevista = buscarEntrevista(entrevistaId);
        if (esTerminal(entrevista.getPostulante())) {
            throw new BusinessException("El proceso ya finalizo.");
        }
        String resultado = normalizar(request.getResultado(), "PENDIENTE");
        if (!"APROBADO".equals(resultado) && !"DESAPROBADO".equals(resultado)) {
            throw new BusinessException("Resultado permitido: APROBADO o DESAPROBADO");
        }
        if (!"REALIZADA".equals(entrevista.getEstadoEntrevista())) {
            entrevista.setEstadoEntrevista("REALIZADA");
        }
        entrevista.setResultado(resultado);
        entrevista.setObservacionInterna(texto(request.getObservacionInterna(), request.getObservacion()));
        entrevista.setObservacionPostulante(limpiar(request.getObservacionPostulante()));
        registrarAuditoria(entrevista, request);
        entrevistaRepository.save(entrevista);

        String admin = texto(request.getUsuarioAdmin(), entrevista.getUsuarioAdmin());
        String interna = entrevista.getObservacionInterna();
        String visible = entrevista.getObservacionPostulante();
        if (NORMAL.equals(entrevista.getTipoEntrevista())) {
            return "APROBADO".equals(resultado)
                    ? postulanteService.cambiarEstado(entrevista.getPostulanteId(), EstadoCodigo.EVALUACION_PSICOLOGICA.name(), admin, interna, visible)
                    : postulanteService.cambiarEstado(entrevista.getPostulanteId(), EstadoCodigo.RECHAZADO.name(), admin, interna, visible);
        }
        return "APROBADO".equals(resultado)
                ? postulanteService.cambiarEstado(entrevista.getPostulanteId(), EstadoCodigo.ACEPTADO.name(), admin, interna, visible)
                : postulanteService.cambiarEstado(entrevista.getPostulanteId(), EstadoCodigo.RECHAZADO.name(), admin, interna, visible);
    }

    @Override
    @Transactional
    public EntrevistaDTO cancelar(Long entrevistaId, EntrevistaRequest request) {
        EntrevistaPostulante entrevista = buscarEntrevista(entrevistaId);
        if (esTerminal(entrevista.getPostulante())) throw new BusinessException("El proceso ya finalizo.");
        if (!List.of("PROGRAMADA", "REPROGRAMADA").contains(entrevista.getEstadoEntrevista())) {
            throw new BusinessException("Solo se puede cancelar una entrevista programada o reprogramada.");
        }
        entrevista.setEstadoEntrevista("CANCELADA");
        if (request != null) {
            entrevista.setObservacionInterna(texto(request.getObservacionInterna(), request.getObservacion()));
            entrevista.setObservacionPostulante(limpiar(request.getObservacionPostulante()));
            registrarAuditoria(entrevista, request);
        } else {
            entrevista.setActualizadoEn(LocalDateTime.now());
        }
        return entrevistaMapper.convertirADTO(entrevistaRepository.save(entrevista));
    }

    @Override
    @Transactional
    public EntrevistaDTO reprogramar(Long entrevistaId, EntrevistaRequest request) {
        if (request == null) throw new BusinessException("Datos de reprogramacion requeridos");
        EntrevistaPostulante entrevista = buscarEntrevista(entrevistaId);
        if (esTerminal(entrevista.getPostulante())) throw new BusinessException("El proceso ya finalizo.");
        if (!"PROGRAMADA".equals(entrevista.getEstadoEntrevista())) {
            throw new BusinessException("Solo se puede reprogramar una entrevista programada.");
        }
        copiarDatosProgramacion(entrevista, request);
        entrevista.setEstadoEntrevista("REPROGRAMADA");
        entrevista.setObservacionInterna(texto(request.getObservacionInterna(), request.getObservacion()));
        entrevista.setObservacionPostulante(limpiar(request.getObservacionPostulante()));
        registrarAuditoria(entrevista, request);
        return entrevistaMapper.convertirADTO(entrevistaRepository.save(entrevista));
    }

    private void validarCreacionPorEstado(Postulante postulante, String tipo) {
        String estado = codigoEstado(postulante);
        if (EstadoCodigo.ACEPTADO.name().equals(estado) || EstadoCodigo.RECHAZADO.name().equals(estado)) {
            throw new BusinessException("El proceso ya finalizo.");
        }
        if (NORMAL.equals(tipo) && !List.of(EstadoCodigo.APROBADO_TECNICO.name(), EstadoCodigo.ENTREVISTA.name()).contains(estado)) {
            throw new BusinessException("Primero debe aprobar la evaluacion tecnica.");
        }
        if (PSICOLOGICA.equals(tipo)) {
            boolean entrevistaAprobada = entrevistaRepository.existsByPostulante_IdAndTipoEntrevistaAndResultado(postulante.getId(), NORMAL, "APROBADO");
            if (!entrevistaAprobada && !EstadoCodigo.EVALUACION_PSICOLOGICA.name().equals(estado)) {
                throw new BusinessException("Primero debe aprobar la entrevista.");
            }
        }
    }

    private void validarNoDuplicada(Long postulanteId, String tipo) {
        if (entrevistaRepository.existsByPostulante_IdAndTipoEntrevistaAndEstadoEntrevistaIn(postulanteId, tipo, ACTIVAS)) {
            throw new BusinessException("Ya existe una entrevista activa de este tipo para el postulante.");
        }
    }

    private void copiarDatosProgramacion(EntrevistaPostulante entrevista, EntrevistaRequest request) {
        LocalDateTime fecha = request.getFechaProgramada() != null ? request.getFechaProgramada() : request.getFechaEntrevista();
        validarFecha(fecha);
        validarHoras(request.getHoraInicio(), request.getHoraFin());
        String modalidad = normalizar(request.getModalidad(), "VIRTUAL");
        if ("PRESENCIAL".equals(modalidad) && esVacio(request.getLugar())) throw new BusinessException("El lugar es obligatorio para entrevistas presenciales.");
        if ("VIRTUAL".equals(modalidad) && esVacio(request.getEnlaceVirtual())) throw new BusinessException("El enlace virtual es obligatorio para entrevistas virtuales.");
        entrevista.setFechaProgramada(fecha);
        entrevista.setHoraInicio(limpiar(request.getHoraInicio()));
        entrevista.setHoraFin(limpiar(request.getHoraFin()));
        entrevista.setModalidad(modalidad);
        entrevista.setLugar(limpiar(request.getLugar()));
        entrevista.setEnlaceVirtual(limpiar(request.getEnlaceVirtual()));
        entrevista.setEntrevistadorNombre(limpiar(request.getEntrevistadorNombre()));
        entrevista.setEntrevistadorCargo(limpiar(request.getEntrevistadorCargo()));
    }

    private void validarFecha(LocalDateTime fecha) {
        if (fecha == null) throw new BusinessException("Fecha programada requerida.");
        LocalDate programada = fecha.toLocalDate();
        if (programada.isBefore(LocalDate.now())) throw new BusinessException("La fecha programada no puede ser anterior a hoy.");
    }

    private void validarHoras(String inicio, String fin) {
        if (!esVacio(inicio) && !esVacio(fin) && inicio.compareTo(fin) >= 0) {
            throw new BusinessException("La hora de inicio debe ser menor que la hora de fin.");
        }
    }

    private void registrarAuditoria(EntrevistaPostulante entrevista, EntrevistaRequest request) {
        entrevista.setActualizadoPorAdmin(buscarAdmin(request.getUsuarioAdmin()));
        entrevista.setActualizadoPorAdminNombre(texto(request.getUsuarioAdmin(), "Admin"));
        entrevista.setObservacionCambio(limpiar(request.getObservacionCambio()));
        entrevista.setActualizadoEn(LocalDateTime.now());
    }

    private Usuario buscarAdmin(String email) {
        if (esVacio(email)) return null;
        return usuarioRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
    }

    private Postulante buscarPostulante(Long id) {
        return postulanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulante no encontrado: " + id));
    }

    private EntrevistaPostulante buscarEntrevista(Long id) {
        return entrevistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrevista no encontrada: " + id));
    }

    private boolean esTerminal(Postulante postulante) {
        String estado = codigoEstado(postulante);
        return EstadoCodigo.ACEPTADO.name().equals(estado) || EstadoCodigo.RECHAZADO.name().equals(estado);
    }

    private String codigoEstado(Postulante postulante) {
        return postulante != null && postulante.getEstado() != null ? postulante.getEstado().getCodigo() : "";
    }

    private String mensajeVisible(EntrevistaPostulante entrevista) {
        String tipo = NORMAL.equals(entrevista.getTipoEntrevista()) ? "entrevista" : "evaluacion psicologica";
        return "Tu " + tipo + " fue programada.";
    }

    private String normalizar(String valor, String fallback) {
        return esVacio(valor) ? fallback : valor.trim().toUpperCase(Locale.ROOT);
    }

    private String texto(String valor, String fallback) {
        return esVacio(valor) ? (esVacio(fallback) ? null : fallback.trim()) : valor.trim();
    }

    private String limpiar(String valor) {
        return esVacio(valor) ? null : valor.trim();
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
