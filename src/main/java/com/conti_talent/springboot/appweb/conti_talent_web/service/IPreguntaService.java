package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PreguntaDTO;

import java.util.List;

public interface IPreguntaService {

    List<PreguntaDTO> listar();

    List<PreguntaDTO> listarPorOferta(Long ofertaId);

    List<PreguntaDTO> listarPorOfertaPublico(Long ofertaId);

    PreguntaDTO obtener(Long id);

    PreguntaDTO crear(PreguntaDTO dto);

    PreguntaDTO actualizar(Long id, PreguntaDTO dto);

    void eliminar(Long id);
}
