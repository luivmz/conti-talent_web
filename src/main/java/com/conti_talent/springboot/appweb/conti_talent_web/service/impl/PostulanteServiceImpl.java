package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import java.time.LocalDateTime;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostulanteAdminRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.EstadoInvalidoException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.PostulanteMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.HistorialEstadoPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IHistorialEstadoPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IUsuarioRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.EvaluacionCompuestaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostulanteServiceImpl implements IPostulanteService {

    private final IPostulanteRepository postulanteRepository;
    private final IOfertaRepository ofertaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IEstadoRepository estadoRepository;
    private final IHistorialEstadoPostulanteRepository historialRepository;
    private final EvaluacionCompuestaService evaluacionCompuestaService;
    private final PostulanteMapper postulanteMapper;

    public PostulanteServiceImpl(IPostulanteRepository postulanteRepository,
                                 IOfertaRepository ofertaRepository,
                                 IUsuarioRepository usuarioRepository,
                                 IEstadoRepository estadoRepository,
                                 IHistorialEstadoPostulanteRepository historialRepository,
                                 EvaluacionCompuestaService evaluacionCompuestaService,
                                 PostulanteMapper postulanteMapper) {
        this.postulanteRepository = postulanteRepository;
        this.ofertaRepository = ofertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
        this.historialRepository = historialRepository;
        this.evaluacionCompuestaService = evaluacionCompuestaService;
        this.postulanteMapper = postulanteMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulanteDTO> listarTodos() {
        return postulanteMapper.convertirALista(postulanteRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public PostulanteDTO obtenerPorId(Long id) {
        return postulanteMapper.convertirADTO(buscarPostulanteOFallar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulanteDTO> listarPorOferta(Long ofertaId) {
        return postulanteMapper.convertirALista(postulanteRepository.findByOfertaId(ofertaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulanteDTO> listarPorUsuario(Long usuarioId) {
        return postulanteMapper.convertirALista(postulanteRepository.findByUsuarioId(usuarioId));
    }

    @Override
    @Transactional
    public PostulanteDTO registrarPostulacion(PostularRequest request) {
        validarDatosPostulacion(request);
        Oferta oferta = ofertaRepository.findById(request.getOfertaId())
                .orElseThrow(() -> new BusinessException("Oferta inexistente: " + request.getOfertaId()));
        Estado estadoInicial = obtenerEstadoPorCodigoOFallar(EstadoCodigo.POSTULADO.name());

        Postulante nuevoPostulante = postulanteMapper.crearDesdeRequest(request);
        nuevoPostulante.setOferta(oferta);
        nuevoPostulante.setEstado(estadoInicial);
        if (request.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new BusinessException("Usuario inexistente: " + request.getUsuarioId()));
            nuevoPostulante.setUsuario(usuario);
        }
        LocalDateTime ahora = LocalDateTime.now();
        nuevoPostulante.setFechaPostulacion(ahora);
        nuevoPostulante.setCreadoEn(ahora);
        evaluacionCompuestaService.recalcular(nuevoPostulante);
        Postulante guardado = postulanteRepository.save(nuevoPostulante);
        registrarHistorial(guardado, null, estadoInicial.getCodigo(), "Sistema",
                "Postulacion registrada", "Tu postulacion fue recibida correctamente.");
        return postulanteMapper.convertirADTO(guardado);
    }

    @Override
    @Transactional
    public PostulanteDTO actualizarDesdeAdmin(Long idPostulante, PostulanteAdminRequest request) {
        if (request == null) throw new BusinessException("Datos de postulante requeridos");
        Postulante postulante = buscarPostulanteOFallar(idPostulante);

        if (request.getOfertaId() != null
                && (postulante.getOfertaId() == null || !postulante.getOfertaId().equals(request.getOfertaId()))) {
            Oferta oferta = ofertaRepository.findById(request.getOfertaId())
                    .orElseThrow(() -> new BusinessException("Oferta inexistente: " + request.getOfertaId()));
            postulante.setOferta(oferta);
        }
        if (!esTextoVacio(request.getNombre())) postulante.setNombre(request.getNombre().trim());
        if (!esTextoVacio(request.getEmail())) postulante.setEmail(request.getEmail().trim());
        if (request.getTelefono() != null) postulante.setTelefono(request.getTelefono().trim());
        if (request.getExperiencia() != null) postulante.setExperiencia(request.getExperiencia().trim());
        if (request.getHabilidades() != null) postulante.setHabilidades(request.getHabilidades().trim());
        if (request.getCv() != null) postulante.setCv(request.getCv().trim());
        if (request.getPuntaje() != null) postulante.setPuntaje(clampPuntaje(request.getPuntaje()));
        if (request.getAniosExperiencia() != null) postulante.setAniosExperiencia(request.getAniosExperiencia());
        if (request.getNivelEstudios() != null) postulante.setNivelEstudios(request.getNivelEstudios().trim());
        if (request.getCarrera() != null) postulante.setCarrera(request.getCarrera().trim());
        if (request.getDisponibilidad() != null) postulante.setDisponibilidad(request.getDisponibilidad().trim());
        if (request.getModalidadPreferida() != null) postulante.setModalidadPreferida(request.getModalidadPreferida().trim());
        if (request.getPretensionSalarial() != null) postulante.setPretensionSalarial(request.getPretensionSalarial());
        if (request.getLinkedin() != null) postulante.setLinkedin(request.getLinkedin().trim());
        if (request.getPortafolio() != null) postulante.setPortafolio(request.getPortafolio().trim());
        if (request.getObservacionAdmin() != null) postulante.setObservacionAdmin(request.getObservacionAdmin().trim());

        evaluacionCompuestaService.recalcular(postulante);
        return postulanteMapper.convertirADTO(postulanteRepository.save(postulante));
    }

    @Override
    @Transactional
    public PostulanteDTO cambiarEstado(Long idPostulante, String estadoDestino) {
        return cambiarEstado(idPostulante, estadoDestino, "Admin", null, null);
    }

    @Override
    @Transactional
    public PostulanteDTO cambiarEstado(Long idPostulante, String estadoDestino, String usuarioAdmin,
                                       String observacionInterna, String observacionPostulante) {
        Postulante postulante = buscarPostulanteOFallar(idPostulante);
        if (esTextoVacio(estadoDestino)) {
            throw new BusinessException("Estado destino requerido");
        }
        Estado estadoActual = postulante.getEstado();
        Estado estadoNuevo  = resolverEstadoDestino(estadoDestino);

        EstadoCodigo codigoActual = EstadoCodigo.valueOf(estadoActual.getCodigo());
        EstadoCodigo codigoNuevo  = EstadoCodigo.valueOf(estadoNuevo.getCodigo());

        if (!codigoActual.puedeTransicionarA(codigoNuevo)) {
            throw new EstadoInvalidoException(
                    "Transicion no permitida: " + codigoActual + " -> " + codigoNuevo);
        }
        postulante.setEstado(estadoNuevo);
        Postulante guardado = postulanteRepository.save(postulante);
        registrarHistorial(guardado, codigoActual.name(), codigoNuevo.name(), usuarioAdmin,
                observacionInterna, observacionPostulante);
        return postulanteMapper.convertirADTO(guardado);
    }

    @Override
    @Transactional
    public PostulanteDTO actualizarObservacionAdmin(Long idPostulante, String observacionAdmin) {
        Postulante postulante = buscarPostulanteOFallar(idPostulante);
        postulante.setObservacionAdmin(observacionAdmin != null ? observacionAdmin.trim() : null);
        return postulanteMapper.convertirADTO(postulanteRepository.save(postulante));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!postulanteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Postulante no encontrado: " + id);
        }
        postulanteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PostulanteDTO marcarComoRechazado(Long id) {
        return cambiarEstado(id, EstadoCodigo.RECHAZADO.name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulanteDTO> obtenerRankingPorPuntaje(Long ofertaId) {
        return obtenerRanking(ofertaId, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostulanteDTO> obtenerRanking(Long ofertaId, String estado, Long areaId) {
        List<Postulante> base = ofertaId == null
                ? postulanteRepository.findAll()
                : postulanteRepository.findByOfertaId(ofertaId);
        return base.stream()
                .filter(postulante -> esTextoVacio(estado)
                        || (postulante.getEstado() != null && estado.equalsIgnoreCase(postulante.getEstado().getCodigo())))
                .filter(postulante -> areaId == null
                        || (postulante.getOferta() != null && postulante.getOferta().getAreaId() != null
                        && postulante.getOferta().getAreaId().equals(areaId)))
                .sorted(Comparator.comparingInt(Postulante::getPuntajeFinal).reversed())
                .map(postulanteMapper::convertirADTO)
                .collect(Collectors.toList());
    }

    /* ============ Helpers privados ============ */

    private Postulante buscarPostulanteOFallar(Long id) {
        return postulanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulante no encontrado: " + id));
    }

    private Estado obtenerEstadoPorCodigoOFallar(String codigo) {
        return estadoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estado con codigo '" + codigo + "' no encontrado"));
    }

    /** Acepta tanto un id numerico como un codigo logico (POSTULADO, ENTREVISTA...). */
    private Estado resolverEstadoDestino(String referencia) {
        try {
            Long idEstado = Long.parseLong(referencia);
            return estadoRepository.findById(idEstado)
                    .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado: " + idEstado));
        } catch (NumberFormatException e) {
            return obtenerEstadoPorCodigoOFallar(referencia);
        }
    }

    private static void validarDatosPostulacion(PostularRequest request) {
        if (request == null)                          throw new BusinessException("Datos de postulacion requeridos");
        if (request.getOfertaId() == null)            throw new BusinessException("ofertaId requerido");
        if (esTextoVacio(request.getNombre()))        throw new BusinessException("Nombre requerido");
        if (esTextoVacio(request.getEmail()))         throw new BusinessException("Email requerido");
        if (request.getAniosExperiencia() < 0)        throw new BusinessException("aniosExperiencia no puede ser negativo");
    }

    private void registrarHistorial(Postulante postulante, String anterior, String nuevo, String usuarioAdmin,
                                    String observacionInterna, String observacionPostulante) {
        HistorialEstadoPostulante historial = new HistorialEstadoPostulante();
        historial.setPostulante(postulante);
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(nuevo);
        historial.setUsuarioAdmin(esTextoVacio(usuarioAdmin) ? "Sistema" : usuarioAdmin.trim());
        historial.setObservacionInterna(observacionInterna);
        historial.setObservacionPostulante(observacionPostulante);
        historial.setFechaCambio(LocalDateTime.now());
        historialRepository.save(historial);
        postulante.getHistorialEstados().add(historial);
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private static int clampPuntaje(Integer puntaje) {
        if (puntaje == null) return 0;
        return Math.max(0, Math.min(100, puntaje));
    }
}
