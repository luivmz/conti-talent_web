package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.time.LocalDateTime;

public class EntrevistaDTO {
    private Long id;
    private Long postulanteId;
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
    private String observacionInterna;
    private String observacionPostulante;
    private String observacion;
    private String usuarioAdmin;
    private Long creadoPorAdminId;
    private Long actualizadoPorAdminId;
    private String actualizadoPorAdmin;
    private String observacionCambio;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostulanteId() { return postulanteId; }
    public void setPostulanteId(Long postulanteId) { this.postulanteId = postulanteId; }

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

    public String getObservacionInterna() { return observacionInterna; }
    public void setObservacionInterna(String observacionInterna) { this.observacionInterna = observacionInterna; }

    public String getObservacionPostulante() { return observacionPostulante; }
    public void setObservacionPostulante(String observacionPostulante) { this.observacionPostulante = observacionPostulante; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getUsuarioAdmin() { return usuarioAdmin; }
    public void setUsuarioAdmin(String usuarioAdmin) { this.usuarioAdmin = usuarioAdmin; }

    public Long getCreadoPorAdminId() { return creadoPorAdminId; }
    public void setCreadoPorAdminId(Long creadoPorAdminId) { this.creadoPorAdminId = creadoPorAdminId; }

    public Long getActualizadoPorAdminId() { return actualizadoPorAdminId; }
    public void setActualizadoPorAdminId(Long actualizadoPorAdminId) { this.actualizadoPorAdminId = actualizadoPorAdminId; }

    public String getActualizadoPorAdmin() { return actualizadoPorAdmin; }
    public void setActualizadoPorAdmin(String actualizadoPorAdmin) { this.actualizadoPorAdmin = actualizadoPorAdmin; }

    public String getObservacionCambio() { return observacionCambio; }
    public void setObservacionCambio(String observacionCambio) { this.observacionCambio = observacionCambio; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
