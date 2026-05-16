package com.conti_talent.springboot.appweb.conti_talent_web.dto.request;

public class PostulanteAdminRequest {

    private Long ofertaId;
    private String nombre;
    private String email;
    private String telefono;
    private String experiencia;
    private String habilidades;
    private String cv;
    private Integer puntaje;
    private Integer aniosExperiencia;
    private String nivelEstudios;
    private String carrera;
    private String disponibilidad;
    private String modalidadPreferida;
    private Double pretensionSalarial;
    private String linkedin;
    private String portafolio;
    private String observacionAdmin;

    public PostulanteAdminRequest() {
    }

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

    public Integer getPuntaje() { return puntaje; }
    public void setPuntaje(Integer puntaje) { this.puntaje = puntaje; }

    public Integer getAniosExperiencia() { return aniosExperiencia; }
    public void setAniosExperiencia(Integer aniosExperiencia) { this.aniosExperiencia = aniosExperiencia; }

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
}
