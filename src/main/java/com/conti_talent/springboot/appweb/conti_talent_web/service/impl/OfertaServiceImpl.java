package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import java.time.LocalDateTime;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.OfertaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.OfertaMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IAreaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPreguntaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IOfertaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfertaServiceImpl implements IOfertaService {

    private static final List<String> TIPOS_VALIDOS = List.of("Práctica", "Trabajo", "Practica");

    private final IOfertaRepository ofertaRepository;
    private final IAreaRepository areaRepository;
    private final IPreguntaRepository preguntaRepository;
    private final OfertaMapper mapper;

    public OfertaServiceImpl(IOfertaRepository ofertaRepository,
                             IAreaRepository areaRepository,
                             IPreguntaRepository preguntaRepository,
                             OfertaMapper mapper) {
        this.ofertaRepository = ofertaRepository;
        this.areaRepository = areaRepository;
        this.preguntaRepository = preguntaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfertaDTO> listar() {
        return mapper.toDTOList(ofertaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfertaDTO> listarPorArea(Long areaId) {
        return mapper.toDTOList(ofertaRepository.findByAreaId(areaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfertaDTO> destacadas() {
        return mapper.toDTOList(ofertaRepository.findByDestacadaTrue());
    }

    @Override
    @Transactional(readOnly = true)
    public OfertaDTO obtener(Long id) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrada: " + id));
        return mapper.toDTO(oferta);
    }

    @Override
    @Transactional
    public OfertaDTO crear(OfertaDTO dto) {
        validarDTO(dto, true);
        Oferta oferta = new Oferta();
        aplicarCambios(oferta, dto, true);
        oferta.setCreadaEn(LocalDateTime.now());
        return mapper.toDTO(ofertaRepository.save(oferta));
    }

    @Override
    @Transactional
    public OfertaDTO actualizar(Long id, OfertaDTO dto) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrada: " + id));
        validarDTO(dto, false);
        aplicarCambios(oferta, dto, false);
        return mapper.toDTO(ofertaRepository.save(oferta));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!ofertaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Oferta no encontrada: " + id);
        }
        preguntaRepository.deleteByOfertaId(id);
        ofertaRepository.deleteById(id);
    }

    /* ================ helpers ================ */

    private void validarDTO(OfertaDTO dto, boolean creando) {
        if (dto == null) throw new BusinessException("Datos de oferta requeridos");
        if (creando && esTextoVacio(dto.getTitulo())) {
            throw new BusinessException("El titulo es obligatorio");
        }
        if (dto.getTipo() != null && !TIPOS_VALIDOS.contains(dto.getTipo())) {
            throw new BusinessException("Tipo invalido. Permitidos: " + TIPOS_VALIDOS);
        }
        if (dto.getAreaId() != null && !areaRepository.existsById(dto.getAreaId())) {
            throw new BusinessException("Area inexistente: " + dto.getAreaId());
        }
    }

    private void aplicarCambios(Oferta oferta, OfertaDTO dto, boolean nuevo) {
        if (dto.getTitulo()      != null) oferta.setTitulo(dto.getTitulo().trim());
        if (dto.getTipo()        != null) oferta.setTipo(dto.getTipo());
        else if (nuevo) oferta.setTipo("Trabajo");
        if (dto.getAreaId()      != null) {
            Area area = areaRepository.findById(dto.getAreaId())
                    .orElseThrow(() -> new BusinessException("Area inexistente: " + dto.getAreaId()));
            oferta.setArea(area);
        }
        if (dto.getModalidad()   != null) oferta.setModalidad(dto.getModalidad());
        if (dto.getUbicacion()   != null) oferta.setUbicacion(dto.getUbicacion());
        if (dto.getHorario()     != null) oferta.setHorario(dto.getHorario().trim());
        oferta.setVacantes(Math.max(1, dto.getVacantes() == 0 && nuevo ? 1 : dto.getVacantes()));
        oferta.setDestacada(dto.isDestacada());
        if (dto.getDescripcion() != null) oferta.setDescripcion(dto.getDescripcion().trim());
        if (dto.getRequisitos()  != null) oferta.setRequisitos(new ArrayList<>(dto.getRequisitos()));
        if (dto.getBeneficios()  != null) oferta.setBeneficios(new ArrayList<>(dto.getBeneficios()));
        if (dto.getHabilidadesRequeridas() != null) {
            oferta.setHabilidadesRequeridas(new ArrayList<>(dto.getHabilidadesRequeridas()));
        }
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
