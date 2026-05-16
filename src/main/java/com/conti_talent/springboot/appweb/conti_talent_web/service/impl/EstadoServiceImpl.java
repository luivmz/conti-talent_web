package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.EstadoMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEstadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoServiceImpl implements IEstadoService {

    private final IEstadoRepository estadoRepository;
    private final EstadoMapper estadoMapper;

    public EstadoServiceImpl(IEstadoRepository estadoRepository, EstadoMapper estadoMapper) {
        this.estadoRepository = estadoRepository;
        this.estadoMapper = estadoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoDTO> listarTodos() {
        return estadoMapper.convertirALista(estadoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoDTO> listarSoloActivos() {
        return estadoRepository.findAll().stream()
                .filter(Estado::isActivo)
                .sorted((a, b) -> Integer.compare(a.getOrden(), b.getOrden()))
                .map(estadoMapper::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoDTO obtenerPorId(Long id) {
        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado: " + id));
        return estadoMapper.convertirADTO(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public Estado obtenerEntidadPorCodigo(String codigo) {
        return estadoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estado con codigo '" + codigo + "' no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean puedeTransicionar(Long idEstadoOrigen, Long idEstadoDestino) {
        Estado origen  = estadoRepository.findById(idEstadoOrigen)
                .orElseThrow(() -> new ResourceNotFoundException("Estado origen no encontrado: " + idEstadoOrigen));
        Estado destino = estadoRepository.findById(idEstadoDestino)
                .orElseThrow(() -> new ResourceNotFoundException("Estado destino no encontrado: " + idEstadoDestino));
        return EstadoCodigo.valueOf(origen.getCodigo())
                .puedeTransicionarA(EstadoCodigo.valueOf(destino.getCodigo()));
    }
}
