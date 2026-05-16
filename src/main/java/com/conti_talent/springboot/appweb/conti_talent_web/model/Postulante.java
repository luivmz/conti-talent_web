package com.conti_talent.springboot.appweb.conti_talent_web.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Postulante a una oferta. Tabla TBL_POSTULANTE.
 *
 * Relaciones:
 *   - ManyToOne a Usuario (nullable, postulante puede ser anonimo o registrado).
 *   - ManyToOne a Oferta y Estado (obligatorias).
 *
 * Las respuestas de la evaluacion tecnica se modelan como una relacion 1:N
 * hacia la entidad intermedia {@link RespuestaPostulante} (resuelve la N:M
 * conceptual Postulante - Pregunta). Hacia afuera el Postulante sigue
 * exponiendo la API legada {@code Map<preguntaId, opcionElegida>} para no
 * romper services, mappers ni el frontend.
 *
 * Las habilidades del postulante se conservan como texto libre en
 * {@code habilidades} (campo descriptivo del CV). Si en el futuro se desea
 * un catalogo controlado, se puede crear una entidad PostulanteHabilidad
 * siguiendo el mismo patron usado para OfertaHabilidad.
 */
@Entity
@Table(name = "tbl_postulante")
public class Postulante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_postulante_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_postulante_oferta"))
    private Oferta oferta;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estado_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_postulante_estado"))
    private Estado estado;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "experiencia", columnDefinition = "TEXT")
    private String experiencia;

    @Column(name = "habilidades", columnDefinition = "TEXT")
    private String habilidades;

    @Column(name = "cv", length = 255)
    private String cv;

    @Column(name = "fecha_postulacion", nullable = false)
    private LocalDateTime fechaPostulacion;

    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;

    @Column(name = "anios_experiencia", nullable = false)
    private int aniosExperiencia;

    @Column(name = "nivel_estudios", length = 80)
    private String nivelEstudios;

    @Column(name = "carrera", length = 120)
    private String carrera;

    @Column(name = "disponibilidad", length = 80)
    private String disponibilidad;

    @Column(name = "modalidad_preferida", length = 40)
    private String modalidadPreferida;

    @Column(name = "pretension_salarial")
    private Double pretensionSalarial;

    @Column(name = "linkedin", length = 255)
    private String linkedin;

    @Column(name = "portafolio", length = 255)
    private String portafolio;

    @Column(name = "observacion_admin", columnDefinition = "TEXT")
    private String observacionAdmin;

    @Column(name = "puntaje_cuestionario", nullable = false)
    private int puntaje;

    @Column(name = "puntaje_experiencia", nullable = false)
    private int puntajeExperiencia;

    @Column(name = "puntaje_habilidades", nullable = false)
    private int puntajeHabilidades;

    @Column(name = "puntaje_final", nullable = false)
    private int puntajeFinal;

    @OneToMany(mappedBy = "postulante", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RespuestaPostulante> respuestasEntidades = new ArrayList<>();

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "postulante", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaSubida DESC")
    private List<DocumentoPostulante> documentos = new ArrayList<>();

    @OneToMany(mappedBy = "postulante", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaCambio DESC")
    private List<HistorialEstadoPostulante> historialEstados = new ArrayList<>();

    @OneToMany(mappedBy = "postulante", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaProgramada DESC")
    private List<EntrevistaPostulante> entrevistas = new ArrayList<>();

    @OneToMany(mappedBy = "postulante", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaEvaluacion DESC")
    private List<EvaluacionPsicologicaPostulante> evaluacionesPsicologicas = new ArrayList<>();

    public Postulante() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Long getUsuarioId() { return usuario != null ? usuario.getId() : null; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public Long getOfertaId() { return oferta != null ? oferta.getId() : null; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Long getEstadoId() { return estado != null ? estado.getId() : null; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getHabilidades() { return habilidades; }
    public void setHabilidades(String habilidades) { this.habilidades = habilidades; }

    public String getCv() { return cv; }
    public void setCv(String cv) { this.cv = cv; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public LocalDateTime getFechaPostulacion() { return fechaPostulacion; }
    public void setFechaPostulacion(LocalDateTime fechaPostulacion) { this.fechaPostulacion = fechaPostulacion; }

    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }

    public int getAniosExperiencia() { return aniosExperiencia; }
    public void setAniosExperiencia(int aniosExperiencia) { this.aniosExperiencia = Math.max(0, aniosExperiencia); }

    public String getNivelEstudios() { return nivelEstudios; }
    public void setNivelEstudios(String nivelEstudios) { this.nivelEstudios = nivelEstudios; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }

    public String getModalidadPreferida() { return modalidadPreferida; }
    public void setModalidadPreferida(String modalidadPreferida) { this.modalidadPreferida = modalidadPreferida; }

    public Double getPretensionSalarial() { return pretensionSalarial; }
    public void setPretensionSalarial(Double pretensionSalarial) { this.pretensionSalarial = pretensionSalarial; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }

    public String getPortafolio() { return portafolio; }
    public void setPortafolio(String portafolio) { this.portafolio = portafolio; }

    public String getObservacionAdmin() { return observacionAdmin; }
    public void setObservacionAdmin(String observacionAdmin) { this.observacionAdmin = observacionAdmin; }

    public int getPuntajeExperiencia() { return puntajeExperiencia; }
    public void setPuntajeExperiencia(int puntajeExperiencia) { this.puntajeExperiencia = puntajeExperiencia; }

    public int getPuntajeHabilidades() { return puntajeHabilidades; }
    public void setPuntajeHabilidades(int puntajeHabilidades) { this.puntajeHabilidades = puntajeHabilidades; }

    public int getPuntajeFinal() { return puntajeFinal; }
    public void setPuntajeFinal(int puntajeFinal) { this.puntajeFinal = puntajeFinal; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public List<DocumentoPostulante> getDocumentos() { return documentos; }
    public void setDocumentos(List<DocumentoPostulante> documentos) {
        this.documentos = documentos != null ? new ArrayList<>(documentos) : new ArrayList<>();
    }

    public List<HistorialEstadoPostulante> getHistorialEstados() { return historialEstados; }
    public void setHistorialEstados(List<HistorialEstadoPostulante> historialEstados) {
        this.historialEstados = historialEstados != null ? new ArrayList<>(historialEstados) : new ArrayList<>();
    }

    public List<EntrevistaPostulante> getEntrevistas() { return entrevistas; }
    public void setEntrevistas(List<EntrevistaPostulante> entrevistas) {
        this.entrevistas = entrevistas != null ? new ArrayList<>(entrevistas) : new ArrayList<>();
    }

    public List<EvaluacionPsicologicaPostulante> getEvaluacionesPsicologicas() { return evaluacionesPsicologicas; }
    public void setEvaluacionesPsicologicas(List<EvaluacionPsicologicaPostulante> evaluacionesPsicologicas) {
        this.evaluacionesPsicologicas = evaluacionesPsicologicas != null
                ? new ArrayList<>(evaluacionesPsicologicas)
                : new ArrayList<>();
    }

    /* =========================================================
     * Respuestas: API publica compatible (Map<preguntaId, opcion>)
     * Internamente trabaja sobre la entidad RespuestaPostulante.
     * ========================================================= */

    public List<RespuestaPostulante> getRespuestasEntidades() { return respuestasEntidades; }

    public Map<Long, Integer> getRespuestas() {
        Map<Long, Integer> mapa = new HashMap<>();
        for (RespuestaPostulante respuesta : respuestasEntidades) {
            if (respuesta.getPreguntaId() != null) {
                mapa.put(respuesta.getPreguntaId(), respuesta.getOpcionElegida());
            }
        }
        return mapa;
    }

    public void setRespuestas(Map<Long, Integer> respuestas) {
        this.respuestasEntidades.clear();
        if (respuestas == null) return;
        for (Map.Entry<Long, Integer> entrada : respuestas.entrySet()) {
            if (entrada.getKey() == null || entrada.getValue() == null) continue;
            this.respuestasEntidades.add(
                    new RespuestaPostulante(this, entrada.getKey(), entrada.getValue()));
        }
    }
}
