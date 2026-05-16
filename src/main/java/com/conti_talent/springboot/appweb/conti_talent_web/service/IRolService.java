package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.RolDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;

import java.util.List;

public interface IRolService {

    List<RolDTO> listarTodos();

    List<RolDTO> listarSoloActivos();

    RolDTO obtenerPorId(Long id);

    Rol obtenerEntidadPorCodigo(String codigo);

    RolDTO crear(RolDTO dto);

    RolDTO actualizar(Long id, RolDTO dto);

    void eliminar(Long id);
}
