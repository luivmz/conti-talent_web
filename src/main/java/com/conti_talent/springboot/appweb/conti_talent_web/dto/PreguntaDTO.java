package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.util.ArrayList;
import java.util.List;

public class PreguntaDTO {

    private Long id;
    private Long ofertaId;
    private String pregunta;
    private List<String> opciones;
    private Integer correcta;

    public PreguntaDTO() {
        this.opciones = new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOfertaId() { return ofertaId; }
    public void setOfertaId(Long ofertaId) { this.ofertaId = ofertaId; }

    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }

    public List<String> getOpciones() { return opciones; }
    public void setOpciones(List<String> opciones) {
        this.opciones = opciones != null ? new ArrayList<>(opciones) : new ArrayList<>();
    }

    public Integer getCorrecta() { return correcta; }
    public void setCorrecta(Integer correcta) { this.correcta = correcta; }
}
