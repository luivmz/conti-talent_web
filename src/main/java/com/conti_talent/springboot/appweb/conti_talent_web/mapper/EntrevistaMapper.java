package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EntrevistaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.EntrevistaPostulante;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntrevistaMapper {
    public EntrevistaDTO convertirADTO(EntrevistaPostulante entrevista) {
        if (entrevista == null) return null;
        EntrevistaDTO dto = new EntrevistaDTO();
        dto.setId(entrevista.getId());
        dto.setPostulanteId(entrevista.getPostulanteId());
        dto.setTipoEntrevista(entrevista.getTipoEntrevista());
        dto.setFechaProgramada(entrevista.getFechaProgramada());
        dto.setFechaEntrevista(entrevista.getFechaProgramada());
        dto.setHoraInicio(entrevista.getHoraInicio());
        dto.setHoraFin(entrevista.getHoraFin());
        dto.setModalidad(entrevista.getModalidad());
        dto.setLugar(entrevista.getLugar());
        dto.setEnlaceVirtual(entrevista.getEnlaceVirtual());
        dto.setEntrevistadorNombre(entrevista.getEntrevistadorNombre());
        dto.setEntrevistadorCargo(entrevista.getEntrevistadorCargo());
        dto.setEstadoEntrevista(entrevista.getEstadoEntrevista());
        dto.setResultado(entrevista.getResultado());
        dto.setObservacionInterna(entrevista.getObservacionInterna());
        dto.setObservacionPostulante(entrevista.getObservacionPostulante());
        dto.setObservacion(entrevista.getObservacionInterna());
        dto.setUsuarioAdmin(entrevista.getUsuarioAdmin());
        dto.setCreadoPorAdminId(entrevista.getCreadoPorAdminId());
        dto.setActualizadoPorAdminId(entrevista.getActualizadoPorAdminId());
        dto.setActualizadoPorAdmin(entrevista.getActualizadoPorAdminNombre());
        dto.setObservacionCambio(entrevista.getObservacionCambio());
        dto.setCreadoEn(entrevista.getCreadoEn());
        dto.setActualizadoEn(entrevista.getActualizadoEn());
        return dto;
    }

    public List<EntrevistaDTO> convertirALista(List<EntrevistaPostulante> entrevistas) {
        if (entrevistas == null) return List.of();
        return entrevistas.stream().map(this::convertirADTO).toList();
    }
}
