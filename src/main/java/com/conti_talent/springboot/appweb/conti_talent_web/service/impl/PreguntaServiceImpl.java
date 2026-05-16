package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PreguntaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.PreguntaMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPreguntaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPreguntaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PreguntaServiceImpl implements IPreguntaService {

    private final IPreguntaRepository preguntaRepository;
    private final IOfertaRepository ofertaRepository;
    private final PreguntaMapper mapper;

    public PreguntaServiceImpl(IPreguntaRepository preguntaRepository,
                               IOfertaRepository ofertaRepository,
                               PreguntaMapper mapper) {
        this.preguntaRepository = preguntaRepository;
        this.ofertaRepository = ofertaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreguntaDTO> listar() {
        return mapper.toAdminDTOList(preguntaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreguntaDTO> listarPorOferta(Long ofertaId) {
        return mapper.toAdminDTOList(preguntaRepository.findByOfertaId(ofertaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreguntaDTO> listarPorOfertaPublico(Long ofertaId) {
        return mapper.toPublicDTOList(preguntaRepository.findByOfertaId(ofertaId));
    }

    @Override
    @Transactional(readOnly = true)
    public PreguntaDTO obtener(Long id) {
        Pregunta pregunta = preguntaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada: " + id));
        return mapper.toAdminDTO(pregunta);
    }

    @Override
    @Transactional
    public PreguntaDTO crear(PreguntaDTO dto) {
        validar(dto);
        Oferta oferta = ofertaRepository.findById(dto.getOfertaId())
                .orElseThrow(() -> new BusinessException("Oferta inexistente: " + dto.getOfertaId()));
        Pregunta pregunta = new Pregunta();
        pregunta.setOferta(oferta);
        pregunta.setPregunta(dto.getPregunta().trim());
        pregunta.setOpciones(new ArrayList<>(dto.getOpciones()));
        pregunta.setCorrecta(dto.getCorrecta());
        return mapper.toAdminDTO(preguntaRepository.save(pregunta));
    }

    @Override
    @Transactional
    public PreguntaDTO actualizar(Long id, PreguntaDTO dto) {
        Pregunta pregunta = preguntaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada: " + id));
        if (dto.getOfertaId() != null) {
            Oferta oferta = ofertaRepository.findById(dto.getOfertaId())
                    .orElseThrow(() -> new BusinessException("Oferta inexistente: " + dto.getOfertaId()));
            pregunta.setOferta(oferta);
        }
        if (dto.getPregunta() != null) pregunta.setPregunta(dto.getPregunta().trim());
        if (dto.getOpciones() != null) pregunta.setOpciones(new ArrayList<>(dto.getOpciones()));
        if (dto.getCorrecta() != null) pregunta.setCorrecta(dto.getCorrecta());
        if (pregunta.getCorrecta() < 0 || pregunta.getCorrecta() >= pregunta.getOpciones().size()) {
            throw new BusinessException("El indice de la respuesta correcta esta fuera de rango");
        }
        return mapper.toAdminDTO(preguntaRepository.save(pregunta));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!preguntaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pregunta no encontrada: " + id);
        }
        preguntaRepository.deleteById(id);
    }

    private void validar(PreguntaDTO dto) {
        if (dto == null) throw new BusinessException("Datos de la pregunta requeridos");
        if (dto.getOfertaId() == null) throw new BusinessException("ofertaId requerido");
        if (esTextoVacio(dto.getPregunta())) throw new BusinessException("Texto de la pregunta requerido");
        if (dto.getOpciones() == null || dto.getOpciones().size() < 2) {
            throw new BusinessException("Se requieren al menos 2 opciones");
        }
        if (dto.getCorrecta() == null || dto.getCorrecta() < 0 || dto.getCorrecta() >= dto.getOpciones().size()) {
            throw new BusinessException("Indice de respuesta correcta invalido");
        }
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
