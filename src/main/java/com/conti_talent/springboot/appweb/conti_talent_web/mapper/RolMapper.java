package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.RolDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RolMapper {

    public RolDTO convertirADTO(Rol rol) {
        if (rol == null) return null;
        return new RolDTO(rol.getId(), rol.getCodigo(), rol.getNombre(),
                rol.getDescripcion(), rol.isActivo(), rol.getCreadoEn());
    }

    public List<RolDTO> convertirALista(List<Rol> roles) {
        if (roles == null) return List.of();
        return roles.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public Rol convertirAEntidad(RolDTO dto) {
        if (dto == null) return null;
        Rol rol = new Rol(dto.getCodigo(), dto.getNombre(), dto.getDescripcion(),
                dto.isActivo(), dto.getCreadoEn());
        rol.setId(dto.getId());
        return rol;
    }
}
