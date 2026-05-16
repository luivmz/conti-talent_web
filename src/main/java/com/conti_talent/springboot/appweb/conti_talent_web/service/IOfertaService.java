package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.OfertaDTO;

import java.util.List;

public interface IOfertaService {

    List<OfertaDTO> listar();

    List<OfertaDTO> listarPorArea(Long areaId);

    List<OfertaDTO> destacadas();

    OfertaDTO obtener(Long id);

    OfertaDTO crear(OfertaDTO dto);

    OfertaDTO actualizar(Long id, OfertaDTO dto);

    void eliminar(Long id);
}
