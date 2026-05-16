package com.conti_talent.springboot.appweb.conti_talent_web.model;

/**
 * Respuesta marcada por un postulante a una pregunta concreta.
 * NO es entidad JPA: las respuestas se persisten via @ElementCollection
 * dentro de TBL_POSTULANTE_RESPUESTA. Esta clase se usa solo como
 * estructura de transporte para reportes detallados.
 */
public class Respuesta {

    private Long preguntaId;
    private int opcionElegida;
    private boolean correcta;

    public Respuesta() {
    }

    public Respuesta(Long preguntaId, int opcionElegida, boolean correcta) {
        this.preguntaId = preguntaId;
        this.opcionElegida = opcionElegida;
        this.correcta = correcta;
    }

    public Long getPreguntaId() { return preguntaId; }
    public void setPreguntaId(Long preguntaId) { this.preguntaId = preguntaId; }

    public int getOpcionElegida() { return opcionElegida; }
    public void setOpcionElegida(int opcionElegida) { this.opcionElegida = opcionElegida; }

    public boolean isCorrecta() { return correcta; }
    public void setCorrecta(boolean correcta) { this.correcta = correcta; }
}
