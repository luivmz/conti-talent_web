package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EvaluacionPsicologicaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.EvaluacionPsicologicaPostulante;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EvaluacionPsicologicaMapper {
    public EvaluacionPsicologicaDTO convertirADTO(EvaluacionPsicologicaPostulante evaluacion) {
        if (evaluacion == null) return null;
        EvaluacionPsicologicaDTO dto = new EvaluacionPsicologicaDTO();
        dto.setId(evaluacion.getId());
        dto.setPostulanteId(evaluacion.getPostulanteId());
        dto.setFechaEvaluacion(evaluacion.getFechaEvaluacion());
        dto.setResultado(evaluacion.getResultado());
        dto.setObservacion(evaluacion.getObservacion());
        dto.setUsuarioAdmin(evaluacion.getUsuarioAdmin());
        dto.setCreadoEn(evaluacion.getCreadoEn());
        return dto;
    }

    public List<EvaluacionPsicologicaDTO> convertirALista(List<EvaluacionPsicologicaPostulante> evaluaciones) {
        if (evaluaciones == null) return List.of();
        return evaluaciones.stream().map(this::convertirADTO).toList();
    }
}
