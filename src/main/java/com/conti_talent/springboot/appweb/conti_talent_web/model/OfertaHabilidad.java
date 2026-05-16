package com.conti_talent.springboot.appweb.conti_talent_web.model;

import jakarta.persistence.*;

/**
 * Entidad intermedia que materializa la relacion N:M conceptual
 * Oferta - Habilidad como una relacion 1:N explicita.
 *
 * Cada fila representa una habilidad requerida por una oferta.
 * El nombre de la habilidad se almacena como texto plano para no obligar
 * a un catalogo maestro de habilidades en esta primera version.
 *
 * Tabla: TBL_OFERTA_HABILIDAD.
 */
@Entity
@Table(name = "tbl_oferta_habilidad")
public class OfertaHabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_habilidad_oferta"))
    private Oferta oferta;

    @Column(name = "habilidad", nullable = false, length = 120)
    private String habilidad;

    @Column(name = "orden", nullable = false)
    private int orden;

    public OfertaHabilidad() {}

    public OfertaHabilidad(Oferta oferta, String habilidad, int orden) {
        this.oferta = oferta;
        this.habilidad = habilidad;
        this.orden = orden;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public String getHabilidad() { return habilidad; }
    public void setHabilidad(String habilidad) { this.habilidad = habilidad; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}
