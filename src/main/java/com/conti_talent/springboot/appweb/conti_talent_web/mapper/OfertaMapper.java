package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.OfertaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OfertaMapper {

    public OfertaDTO toDTO(Oferta oferta) {
        if (oferta == null) return null;
        OfertaDTO dto = new OfertaDTO();
        dto.setId(oferta.getId());
        dto.setTitulo(oferta.getTitulo());
        dto.setTipo(oferta.getTipo());
        dto.setAreaId(oferta.getAreaId());
        dto.setModalidad(oferta.getModalidad());
        dto.setUbicacion(oferta.getUbicacion());
        dto.setHorario(oferta.getHorario());
        dto.setVacantes(oferta.getVacantes());
        dto.setDestacada(oferta.isDestacada());
        dto.setDescripcion(oferta.getDescripcion());
        dto.setRequisitos(oferta.getRequisitos());
        dto.setBeneficios(oferta.getBeneficios());
        dto.setHabilidadesRequeridas(oferta.getHabilidadesRequeridas());
        dto.setCreadaEn(oferta.getCreadaEn());
        return dto;
    }

    public List<OfertaDTO> toDTOList(List<Oferta> ofertas) {
        if (ofertas == null) return List.of();
        return ofertas.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
