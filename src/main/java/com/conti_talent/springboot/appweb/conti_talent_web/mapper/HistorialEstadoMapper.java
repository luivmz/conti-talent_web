package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.HistorialEstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.HistorialEstadoPostulante;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HistorialEstadoMapper {

    public HistorialEstadoDTO convertirADTO(HistorialEstadoPostulante historial) {
        if (historial == null) return null;
        HistorialEstadoDTO dto = new HistorialEstadoDTO();
        dto.setId(historial.getId());
        dto.setPostulanteId(historial.getPostulanteId());
        dto.setEstadoAnterior(historial.getEstadoAnterior());
        dto.setEstadoNuevo(historial.getEstadoNuevo());
        dto.setFechaCambio(historial.getFechaCambio());
        dto.setUsuarioAdmin(historial.getUsuarioAdmin());
        dto.setObservacionInterna(historial.getObservacionInterna());
        dto.setObservacionPostulante(historial.getObservacionPostulante());
        return dto;
    }

    public List<HistorialEstadoDTO> convertirALista(List<HistorialEstadoPostulante> historial) {
        if (historial == null) return List.of();
        return historial.stream().map(this::convertirADTO).toList();
    }
}
