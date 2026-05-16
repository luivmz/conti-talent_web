package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PreguntaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PreguntaMapper {

    /** DTO completo (uso administrativo). Incluye el indice correcto. */
    public PreguntaDTO toAdminDTO(Pregunta pregunta) {
        if (pregunta == null) return null;
        PreguntaDTO dto = new PreguntaDTO();
        dto.setId(pregunta.getId());
        dto.setOfertaId(pregunta.getOfertaId());
        dto.setPregunta(pregunta.getPregunta());
        dto.setOpciones(pregunta.getOpciones());
        dto.setCorrecta(pregunta.getCorrecta());
        return dto;
    }

    /** DTO publico (al postular): omite la respuesta correcta. */
    public PreguntaDTO toPublicDTO(Pregunta pregunta) {
        PreguntaDTO dto = toAdminDTO(pregunta);
        if (dto != null) dto.setCorrecta(null);
        return dto;
    }

    public List<PreguntaDTO> toAdminDTOList(List<Pregunta> preguntas) {
        if (preguntas == null) return List.of();
        return preguntas.stream().map(this::toAdminDTO).collect(Collectors.toList());
    }

    public List<PreguntaDTO> toPublicDTOList(List<Pregunta> preguntas) {
        if (preguntas == null) return List.of();
        return preguntas.stream().map(this::toPublicDTO).collect(Collectors.toList());
    }
}
