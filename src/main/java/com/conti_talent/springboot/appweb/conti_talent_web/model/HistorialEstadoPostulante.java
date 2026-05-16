package com.conti_talent.springboot.appweb.conti_talent_web.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_historial_estado_postulante")
public class HistorialEstadoPostulante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "postulante_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_historial_postulante"))
    private Postulante postulante;

    @Column(name = "estado_anterior", length = 40)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, length = 40)
    private String estadoNuevo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "usuario_admin", length = 120)
    private String usuarioAdmin;

    @Column(name = "observacion_interna", columnDefinition = "TEXT")
    private String observacionInterna;

    @Column(name = "observacion_postulante", columnDefinition = "TEXT")
    private String observacionPostulante;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Postulante getPostulante() { return postulante; }
    public void setPostulante(Postulante postulante) { this.postulante = postulante; }

    public Long getPostulanteId() { return postulante != null ? postulante.getId() : null; }

    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }

    public String getUsuarioAdmin() { return usuarioAdmin; }
    public void setUsuarioAdmin(String usuarioAdmin) { this.usuarioAdmin = usuarioAdmin; }

    public String getObservacionInterna() { return observacionInterna; }
    public void setObservacionInterna(String observacionInterna) { this.observacionInterna = observacionInterna; }

    public String getObservacionPostulante() { return observacionPostulante; }
    public void setObservacionPostulante(String observacionPostulante) { this.observacionPostulante = observacionPostulante; }

    public String getObservacion() { return observacionPostulante != null ? observacionPostulante : observacionInterna; }
    public void setObservacion(String observacion) { this.observacionInterna = observacion; }
}
