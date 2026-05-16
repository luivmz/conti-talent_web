package com.conti_talent.springboot.appweb.conti_talent_web.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Oferta laboral o de practicas. Tabla TBL_OFERTA.
 *
 * Tipo y modalidad se guardan como String para conservar 1:1 los valores
 * que ya consume el frontend ('Practica', 'Trabajo', 'Presencial', etc.).
 *
 * Las relaciones que conceptualmente son N:M (requisitos, beneficios,
 * habilidades) se materializan como 1:N hacia entidades intermedias
 * explicitas:
 *
 *   tbl_oferta_requisito  (OfertaRequisito)
 *   tbl_oferta_beneficio  (OfertaBeneficio)
 *   tbl_oferta_habilidad  (OfertaHabilidad)
 *
 * Los getters/setters publicos siguen exponiendo {@code List<String>} para
 * mantener compatibilidad total con services, mappers y frontend, pero
 * internamente trabajan sobre las colecciones de entidades.
 */
@Entity
@Table(name = "tbl_oferta")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false, length = 120)
    private String titulo;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "area_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_oferta_area"))
    private Area area;

    @Column(name = "modalidad", length = 20)
    private String modalidad;

    @Column(name = "ubicacion", length = 80)
    private String ubicacion;

    @Column(name = "horario", length = 120)
    private String horario;

    @Column(name = "vacantes", nullable = false)
    private int vacantes;

    @Column(name = "destacada", nullable = false)
    private boolean destacada;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orden ASC")
    private List<OfertaRequisito> requisitosEntidades = new ArrayList<>();

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orden ASC")
    private List<OfertaBeneficio> beneficiosEntidades = new ArrayList<>();

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orden ASC")
    private List<OfertaHabilidad> habilidadesEntidades = new ArrayList<>();

    @Column(name = "creada_en", nullable = false)
    private LocalDateTime creadaEn;

    public Oferta() {}

    public Oferta(String titulo, String tipo, Area area, String modalidad,
                  String ubicacion, int vacantes, boolean destacada, String descripcion,
                  List<String> requisitos, List<String> beneficios, LocalDateTime creadaEn) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.area = area;
        this.modalidad = modalidad;
        this.ubicacion = ubicacion;
        this.horario = "";
        this.vacantes = vacantes;
        this.destacada = destacada;
        this.descripcion = descripcion;
        this.creadaEn = creadaEn;
        setRequisitos(requisitos);
        setBeneficios(beneficios);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }

    /** Atajo para servicios que solo necesitan el id. */
    public Long getAreaId() { return area != null ? area.getId() : null; }

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

    public LocalDateTime getCreadaEn() { return creadaEn; }
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }

    /* =========================================================
     * Acceso a las entidades intermedias (uso interno / servicios)
     * ========================================================= */

    public List<OfertaRequisito> getRequisitosEntidades() { return requisitosEntidades; }
    public List<OfertaBeneficio> getBeneficiosEntidades() { return beneficiosEntidades; }
    public List<OfertaHabilidad> getHabilidadesEntidades() { return habilidadesEntidades; }

    /* =========================================================
     * API publica compatible: lista de Strings (no romper frontend)
     * ========================================================= */

    public List<String> getRequisitos() {
        return requisitosEntidades.stream()
                .map(OfertaRequisito::getTexto)
                .collect(Collectors.toList());
    }

    public void setRequisitos(List<String> textos) {
        this.requisitosEntidades.clear();
        if (textos == null) return;
        int orden = 0;
        for (String texto : textos) {
            this.requisitosEntidades.add(new OfertaRequisito(this, texto, orden++));
        }
    }

    public List<String> getBeneficios() {
        return beneficiosEntidades.stream()
                .map(OfertaBeneficio::getTexto)
                .collect(Collectors.toList());
    }

    public void setBeneficios(List<String> textos) {
        this.beneficiosEntidades.clear();
        if (textos == null) return;
        int orden = 0;
        for (String texto : textos) {
            this.beneficiosEntidades.add(new OfertaBeneficio(this, texto, orden++));
        }
    }

    public List<String> getHabilidadesRequeridas() {
        return habilidadesEntidades.stream()
                .map(OfertaHabilidad::getHabilidad)
                .collect(Collectors.toList());
    }

    public void setHabilidadesRequeridas(List<String> habilidades) {
        this.habilidadesEntidades.clear();
        if (habilidades == null) return;
        int orden = 0;
        for (String habilidad : habilidades) {
            this.habilidadesEntidades.add(new OfertaHabilidad(this, habilidad, orden++));
        }
    }
}
