package com.conti_talent.springboot.appweb.conti_talent_web.dto.request;

import java.time.LocalDateTime;

public class EntrevistaRequest {
    private String tipoEntrevista;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaEntrevista;
    private String horaInicio;
    private String horaFin;
    private String modalidad;
    private String lugar;
    private String enlaceVirtual;
    private String entrevistadorNombre;
    private String entrevistadorCargo;
    private String estadoEntrevista;
    private String resultado;
    private String observacion;
    private String observacionInterna;
    private String usuarioAdmin;
    private String observacionPostulante;
    private String observacionCambio;

    public String getTipoEntrevista() { return tipoEntrevista; }
    public void setTipoEntrevista(String tipoEntrevista) { this.tipoEntrevista = tipoEntrevista; }

    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public LocalDateTime getFechaEntrevista() { return fechaEntrevista; }
    public void setFechaEntrevista(LocalDateTime fechaEntrevista) { this.fechaEntrevista = fechaEntrevista; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public String getEnlaceVirtual() { return enlaceVirtual; }
    public void setEnlaceVirtual(String enlaceVirtual) { this.enlaceVirtual = enlaceVirtual; }

    public String getEntrevistadorNombre() { return entrevistadorNombre; }
    public void setEntrevistadorNombre(String entrevistadorNombre) { this.entrevistadorNombre = entrevistadorNombre; }

    public String getEntrevistadorCargo() { return entrevistadorCargo; }
    public void setEntrevistadorCargo(String entrevistadorCargo) { this.entrevistadorCargo = entrevistadorCargo; }

    public String getEstadoEntrevista() { return estadoEntrevista; }
    public void setEstadoEntrevista(String estadoEntrevista) { this.estadoEntrevista = estadoEntrevista; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getObservacionInterna() { return observacionInterna; }
    public void setObservacionInterna(String observacionInterna) { this.observacionInterna = observacionInterna; }

    public String getUsuarioAdmin() { return usuarioAdmin; }
    public void setUsuarioAdmin(String usuarioAdmin) { this.usuarioAdmin = usuarioAdmin; }

    public String getObservacionPostulante() { return observacionPostulante; }
    public void setObservacionPostulante(String observacionPostulante) { this.observacionPostulante = observacionPostulante; }

    public String getObservacionCambio() { return observacionCambio; }
    public void setObservacionCambio(String observacionCambio) { this.observacionCambio = observacionCambio; }
}
