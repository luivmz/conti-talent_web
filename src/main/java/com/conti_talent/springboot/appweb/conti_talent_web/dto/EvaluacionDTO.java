package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EvaluacionDTO {

    private Long postulanteId;
    private Long ofertaId;
    private int puntaje;
    private int correctas;
    private int total;
    private Map<Long, Integer> respuestas;
    private LocalDateTime evaluadoEn;

    public EvaluacionDTO() {
        this.respuestas = new HashMap<>();
    }

    public EvaluacionDTO(Long postulanteId, Long ofertaId, int puntaje,
                         int correctas, int total, Map<Long, Integer> respuestas, LocalDateTime evaluadoEn) {
        this.postulanteId = postulanteId;
        this.ofertaId = ofertaId;
        this.puntaje = puntaje;
        this.correctas = correctas;
        this.total = total;
        this.respuestas = respuestas != null ? new HashMap<>(respuestas) : new HashMap<>();
        this.evaluadoEn = evaluadoEn;
    }

    public Long getPostulanteId() { return postulanteId; }
    public void setPostulanteId(Long postulanteId) { this.postulanteId = postulanteId; }

    public Long getOfertaId() { return ofertaId; }
    public void setOfertaId(Long ofertaId) { this.ofertaId = ofertaId; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public int getCorrectas() { return correctas; }
    public void setCorrectas(int correctas) { this.correctas = correctas; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public Map<Long, Integer> getRespuestas() { return respuestas; }
    public void setRespuestas(Map<Long, Integer> respuestas) {
        this.respuestas = respuestas != null ? new HashMap<>(respuestas) : new HashMap<>();
    }

    public LocalDateTime getEvaluadoEn() { return evaluadoEn; }
    public void setEvaluadoEn(LocalDateTime evaluadoEn) { this.evaluadoEn = evaluadoEn; }
}
