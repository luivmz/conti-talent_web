package com.conti_talent.springboot.appweb.conti_talent_web.dto.request;

import java.util.HashMap;
import java.util.Map;

public class EvaluacionRequest {

    private Long postulanteId;
    /** preguntaId -> indice elegido */
    private Map<Long, Integer> respuestas;

    public EvaluacionRequest() {
        this.respuestas = new HashMap<>();
    }

    public Long getPostulanteId() { return postulanteId; }
    public void setPostulanteId(Long postulanteId) { this.postulanteId = postulanteId; }

    public Map<Long, Integer> getRespuestas() { return respuestas; }
    public void setRespuestas(Map<Long, Integer> respuestas) {
        this.respuestas = respuestas != null ? new HashMap<>(respuestas) : new HashMap<>();
    }
}
