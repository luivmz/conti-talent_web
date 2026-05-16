package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.AreaDTO;

import java.util.List;

public interface IAreaService {

    List<AreaDTO> listar();

    AreaDTO obtener(Long id);

    AreaDTO crear(AreaDTO dto);

    AreaDTO actualizar(Long id, AreaDTO dto);

    void eliminar(Long id);

    int contarOfertas(Long areaId);
}
