package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.AreaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.AreaMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IAreaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IOfertaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IAreaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AreaServiceImpl implements IAreaService {

    private final IAreaRepository areaRepository;
    private final IOfertaRepository ofertaRepository;
    private final AreaMapper mapper;

    public AreaServiceImpl(IAreaRepository areaRepository,
                           IOfertaRepository ofertaRepository,
                           AreaMapper mapper) {
        this.areaRepository = areaRepository;
        this.ofertaRepository = ofertaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaDTO> listar() {
        return mapper.toDTOList(areaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public AreaDTO obtener(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area no encontrada: " + id));
        return mapper.toDTO(area);
    }

    @Override
    @Transactional
    public AreaDTO crear(AreaDTO dto) {
        if (dto == null || esTextoVacio(dto.getNombre())) {
            throw new BusinessException("El nombre del area es obligatorio");
        }
        Area nueva = new Area();
        nueva.setNombre(dto.getNombre().trim());
        nueva.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : "");
        nueva.setIcono(dto.getIcono() != null ? dto.getIcono() : "default");
        nueva.setColor(dto.getColor() != null ? dto.getColor() : "#6366f1");
        return mapper.toDTO(areaRepository.save(nueva));
    }

    @Override
    @Transactional
    public AreaDTO actualizar(Long id, AreaDTO dto) {
        Area existente = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area no encontrada: " + id));
        if (dto.getNombre()      != null) existente.setNombre(dto.getNombre().trim());
        if (dto.getDescripcion() != null) existente.setDescripcion(dto.getDescripcion().trim());
        if (dto.getIcono()       != null) existente.setIcono(dto.getIcono());
        if (dto.getColor()       != null) existente.setColor(dto.getColor());
        return mapper.toDTO(areaRepository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!areaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Area no encontrada: " + id);
        }
        if (!ofertaRepository.findByAreaId(id).isEmpty()) {
            throw new BusinessException("No se puede eliminar el area: tiene ofertas asociadas");
        }
        areaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int contarOfertas(Long areaId) {
        return ofertaRepository.findByAreaId(areaId).size();
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
