package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class OfertaDTO {

    private Long id;
    private String titulo;
    private String tipo;
    private Long areaId;
    private String modalidad;
    private String ubicacion;
    private String horario;
    private int vacantes;
    private boolean destacada;
    private String descripcion;
    private List<String> requisitos;
    private List<String> beneficios;
    private List<String> habilidadesRequeridas;
    private LocalDateTime creadaEn;

    public OfertaDTO() {
        this.requisitos = new ArrayList<>();
        this.beneficios = new ArrayList<>();
        this.habilidadesRequeridas = new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getAreaId() { return areaId; }
    public void setAreaId(Long areaId) { this.areaId = areaId; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public int getVacantes() { return vacantes; }
    public void setVacantes(int vacantes) { this.vacantes = vacantes; }

    public boolean isDestacada() { return destacada; }
    public void setDestacada(boolean destacada) { this.destacada = destacada; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<String> getRequisitos() { return requisitos; }
    public void setRequisitos(List<String> requisitos) {
        this.requisitos = requisitos != null ? new ArrayList<>(requisitos) : new ArrayList<>();
    }

    public List<String> getBeneficios() { return beneficios; }
    public void setBeneficios(List<String> beneficios) {
        this.beneficios = beneficios != null ? new ArrayList<>(beneficios) : new ArrayList<>();
    }

    public List<String> getHabilidadesRequeridas() { return habilidadesRequeridas; }
    public void setHabilidadesRequeridas(List<String> habilidadesRequeridas) {
        this.habilidadesRequeridas = habilidadesRequeridas != null ? new ArrayList<>(habilidadesRequeridas) : new ArrayList<>();
    }

    public LocalDateTime getCreadaEn() { return creadaEn; }
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }
}
