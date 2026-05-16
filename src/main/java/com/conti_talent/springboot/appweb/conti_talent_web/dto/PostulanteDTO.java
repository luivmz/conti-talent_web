package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostulanteDTO {

    private Long id;
    private Long usuarioId;
    private Long ofertaId;
    private String nombre;
    private String email;
    private String telefono;
    private String experiencia;
    private String habilidades;
    private String cv;
    private LocalDateTime fechaPostulacion;
    private LocalDateTime fechaEvaluacion;
    private int aniosExperiencia;
    private String nivelEstudios;
    private String carrera;
    private String disponibilidad;
    private String modalidadPreferida;
    private Double pretensionSalarial;
    private String linkedin;
    private String portafolio;
    private String observacionAdmin;
    private Long estadoId;
    /** Codigo del estado en mayusculas: POSTULADO, EN_EVALUACION, etc. */
    private String estado;
    private EstadoDTO estadoDetalle;
    private int puntaje;
    private int puntajeCuestionario;
    private int puntajeExperiencia;
    private int puntajeHabilidades;
    private int puntajeFinal;
    private Map<Long, Integer> respuestas;
    private LocalDateTime creadoEn;
    private List<DocumentoPostulanteDTO> documentos;
    private List<HistorialEstadoDTO> historialEstados;
    private List<EntrevistaDTO> entrevistas;
    private List<EvaluacionPsicologicaDTO> evaluacionesPsicologicas;

    public PostulanteDTO() {
        this.respuestas = new HashMap<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getOfertaId() { return ofertaId; }
    public void setOfertaId(Long ofertaId) { this.ofertaId = ofertaId; }

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

    public LocalDateTime getFechaPostulacion() { return fechaPostulacion; }
    public void setFechaPostulacion(LocalDateTime fechaPostulacion) { this.fechaPostulacion = fechaPostulacion; }

    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }

    public int getAniosExperiencia() { return aniosExperiencia; }
    public void setAniosExperiencia(int aniosExperiencia) { this.aniosExperiencia = aniosExperiencia; }

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

    public Long getEstadoId() { return estadoId; }
    public void setEstadoId(Long estadoId) { this.estadoId = estadoId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public EstadoDTO getEstadoDetalle() { return estadoDetalle; }
    public void setEstadoDetalle(EstadoDTO estadoDetalle) { this.estadoDetalle = estadoDetalle; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public int getPuntajeCuestionario() { return puntajeCuestionario; }
    public void setPuntajeCuestionario(int puntajeCuestionario) { this.puntajeCuestionario = puntajeCuestionario; }

    public int getPuntajeExperiencia() { return puntajeExperiencia; }
    public void setPuntajeExperiencia(int puntajeExperiencia) { this.puntajeExperiencia = puntajeExperiencia; }

    public int getPuntajeHabilidades() { return puntajeHabilidades; }
    public void setPuntajeHabilidades(int puntajeHabilidades) { this.puntajeHabilidades = puntajeHabilidades; }

    public int getPuntajeFinal() { return puntajeFinal; }
    public void setPuntajeFinal(int puntajeFinal) { this.puntajeFinal = puntajeFinal; }

    public Map<Long, Integer> getRespuestas() { return respuestas; }
    public void setRespuestas(Map<Long, Integer> respuestas) {
        this.respuestas = respuestas != null ? new HashMap<>(respuestas) : new HashMap<>();
    }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public List<DocumentoPostulanteDTO> getDocumentos() { return documentos; }
    public void setDocumentos(List<DocumentoPostulanteDTO> documentos) { this.documentos = documentos; }

    public List<HistorialEstadoDTO> getHistorialEstados() { return historialEstados; }
    public void setHistorialEstados(List<HistorialEstadoDTO> historialEstados) { this.historialEstados = historialEstados; }

    public List<EntrevistaDTO> getEntrevistas() { return entrevistas; }
    public void setEntrevistas(List<EntrevistaDTO> entrevistas) { this.entrevistas = entrevistas; }

    public List<EvaluacionPsicologicaDTO> getEvaluacionesPsicologicas() { return evaluacionesPsicologicas; }
    public void setEvaluacionesPsicologicas(List<EvaluacionPsicologicaDTO> evaluacionesPsicologicas) {
        this.evaluacionesPsicologicas = evaluacionesPsicologicas;
    }
}
