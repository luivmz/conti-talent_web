package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;

import java.util.List;

public interface IEstadoService {

    List<EstadoDTO> listarTodos();

    List<EstadoDTO> listarSoloActivos();

    EstadoDTO obtenerPorId(Long id);

    Estado obtenerEntidadPorCodigo(String codigo);

    boolean puedeTransicionar(Long idEstadoOrigen, Long idEstadoDestino);
}
