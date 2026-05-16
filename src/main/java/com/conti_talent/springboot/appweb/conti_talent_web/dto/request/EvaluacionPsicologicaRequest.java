package com.conti_talent.springboot.appweb.conti_talent_web.dto.request;

import java.time.LocalDateTime;

public class EvaluacionPsicologicaRequest {
    private LocalDateTime fechaEvaluacion;
    private String resultado;
    private String observacion;
    private String usuarioAdmin;
    private String observacionPostulante;

    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getUsuarioAdmin() { return usuarioAdmin; }
    public void setUsuarioAdmin(String usuarioAdmin) { this.usuarioAdmin = usuarioAdmin; }

    public String getObservacionPostulante() { return observacionPostulante; }
    public void setObservacionPostulante(String observacionPostulante) { this.observacionPostulante = observacionPostulante; }
}
