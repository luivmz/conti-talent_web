package com.conti_talent.springboot.appweb.conti_talent_web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado consolidado de una evaluacion tecnica.
 * NO es entidad JPA: las respuestas y el puntaje se persisten en TBL_POSTULANTE.
 * Esta clase encapsula el resultado de calcular una evaluacion antes de
 * traducirlo a EvaluacionDTO.
 */
public class Evaluacion {

    private Long postulanteId;
    private Long ofertaId;
    private int puntaje;
    private int correctas;
    private int total;
    private List<Respuesta> respuestas;
    private long evaluadoEn;

    public Evaluacion() {
        this.respuestas = new ArrayList<>();
    }

    public Evaluacion(Long postulanteId, Long ofertaId, int puntaje,
                      int correctas, int total, List<Respuesta> respuestas, long evaluadoEn) {
        this.postulanteId = postulanteId;
        this.ofertaId = ofertaId;
        this.puntaje = puntaje;
        this.correctas = correctas;
        this.total = total;
        this.respuestas = respuestas != null ? new ArrayList<>(respuestas) : new ArrayList<>();
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

    public List<Respuesta> getRespuestas() { return respuestas; }
    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas != null ? new ArrayList<>(respuestas) : new ArrayList<>();
    }

    public long getEvaluadoEn() { return evaluadoEn; }
    public void setEvaluadoEn(long evaluadoEn) { this.evaluadoEn = evaluadoEn; }
}
