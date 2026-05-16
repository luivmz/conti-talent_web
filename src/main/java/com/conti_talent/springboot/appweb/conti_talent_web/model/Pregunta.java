package com.conti_talent.springboot.appweb.conti_talent_web.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Pregunta del cuestionario tecnico. Tabla TBL_PREGUNTA.
 * Las opciones se persisten como tabla auxiliar TBL_PREGUNTA_OPCION via
 * @ElementCollection.
 */
@Entity
@Table(name = "tbl_pregunta")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pregunta_oferta"))
    private Oferta oferta;

    @Column(name = "enunciado", nullable = false, length = 500)
    private String enunciado;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tbl_pregunta_opcion",
            joinColumns = @JoinColumn(name = "pregunta_id",
                    foreignKey = @ForeignKey(name = "fk_opcion_pregunta")))
    @OrderColumn(name = "indice")
    @Column(name = "texto", length = 255, nullable = false)
    private List<String> opciones;

    @Column(name = "correcta", nullable = false)
    private int correcta;

    public Pregunta() {
        this.opciones = new ArrayList<>();
    }

    public Pregunta(Oferta oferta, String enunciado, List<String> opciones, int correcta) {
        this.oferta = oferta;
        this.enunciado = enunciado;
        this.opciones = opciones != null ? new ArrayList<>(opciones) : new ArrayList<>();
        this.correcta = correcta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public Long getOfertaId() { return oferta != null ? oferta.getId() : null; }

    public String getPregunta() { return enunciado; }
    public void setPregunta(String enunciado) { this.enunciado = enunciado; }

    public List<String> getOpciones() { return opciones; }
    public void setOpciones(List<String> opciones) {
        this.opciones = opciones != null ? new ArrayList<>(opciones) : new ArrayList<>();
    }

    public int getCorrecta() { return correcta; }
    public void setCorrecta(int correcta) { this.correcta = correcta; }
}
