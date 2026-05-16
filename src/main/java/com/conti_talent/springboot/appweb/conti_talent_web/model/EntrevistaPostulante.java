package com.conti_talent.springboot.appweb.conti_talent_web.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_entrevista_postulante")
public class EntrevistaPostulante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "postulante_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_entrevista_postulante"))
    private Postulante postulante;

    @Column(name = "tipo_entrevista", nullable = false, length = 40)
    private String tipoEntrevista;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "hora_inicio", length = 5)
    private String horaInicio;

    @Column(name = "hora_fin", length = 5)
    private String horaFin;

    @Column(name = "modalidad", nullable = false, length = 30)
    private String modalidad;

    @Column(name = "lugar", length = 255)
    private String lugar;

    @Column(name = "enlace_virtual", length = 500)
    private String enlaceVirtual;

    @Column(name = "entrevistador_nombre", length = 120)
    private String entrevistadorNombre;

    @Column(name = "entrevistador_cargo", length = 120)
    private String entrevistadorCargo;

    @Column(name = "estado_entrevista", nullable = false, length = 30)
    private String estadoEntrevista;

    @Column(name = "resultado", nullable = false, length = 30)
    private String resultado;

    @Column(name = "observacion_interna", columnDefinition = "TEXT")
    private String observacionInterna;

    @Column(name = "observacion_postulante", columnDefinition = "TEXT")
    private String observacionPostulante;

    @Column(name = "usuario_admin", length = 120)
    private String usuarioAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_admin_id",
            foreignKey = @ForeignKey(name = "fk_entrevista_admin_creador"))
    private Usuario creadoPorAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actualizado_por_admin_id",
            foreignKey = @ForeignKey(name = "fk_entrevista_admin_editor"))
    private Usuario actualizadoPorAdmin;

    @Column(name = "actualizado_por_admin", length = 120)
    private String actualizadoPorAdminNombre;

    @Column(name = "observacion_cambio", columnDefinition = "TEXT")
    private String observacionCambio;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Postulante getPostulante() { return postulante; }
    public void setPostulante(Postulante postulante) { this.postulante = postulante; }

    public Long getPostulanteId() { return postulante != null ? postulante.getId() : null; }

    public String getTipoEntrevista() { return tipoEntrevista; }
    public void setTipoEntrevista(String tipoEntrevista) { this.tipoEntrevista = tipoEntrevista; }

    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public LocalDateTime getFechaEntrevista() { return fechaProgramada; }
    public void setFechaEntrevista(LocalDateTime fechaEntrevista) { this.fechaProgramada = fechaEntrevista; }

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

    public String getObservacion() { return observacionInterna; }
    public void setObservacion(String observacion) { this.observacionInterna = observacion; }

    public String getUsuarioAdmin() { return usuarioAdmin; }
    public void setUsuarioAdmin(String usuarioAdmin) { this.usuarioAdmin = usuarioAdmin; }

    public Usuario getCreadoPorAdmin() { return creadoPorAdmin; }
    public void setCreadoPorAdmin(Usuario creadoPorAdmin) { this.creadoPorAdmin = creadoPorAdmin; }

    public Long getCreadoPorAdminId() { return creadoPorAdmin != null ? creadoPorAdmin.getId() : null; }

    public Usuario getActualizadoPorAdmin() { return actualizadoPorAdmin; }
    public void setActualizadoPorAdmin(Usuario actualizadoPorAdmin) { this.actualizadoPorAdmin = actualizadoPorAdmin; }

    public Long getActualizadoPorAdminId() { return actualizadoPorAdmin != null ? actualizadoPorAdmin.getId() : null; }

    public String getActualizadoPorAdminNombre() { return actualizadoPorAdminNombre; }
    public void setActualizadoPorAdminNombre(String actualizadoPorAdminNombre) { this.actualizadoPorAdminNombre = actualizadoPorAdminNombre; }

    public String getObservacionCambio() { return observacionCambio; }
    public void setObservacionCambio(String observacionCambio) { this.observacionCambio = observacionCambio; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
