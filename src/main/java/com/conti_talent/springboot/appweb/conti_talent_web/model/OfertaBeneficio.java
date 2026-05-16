package com.conti_talent.springboot.appweb.conti_talent_web.model;

import jakarta.persistence.*;

/**
 * Entidad intermedia que materializa la relacion N:M conceptual
 * Oferta - Beneficio como una relacion 1:N explicita.
 *
 * Cada fila representa un beneficio especifico ofrecido por una oferta.
 * Tabla: TBL_OFERTA_BENEFICIO.
 */
@Entity
@Table(name = "tbl_oferta_beneficio")
public class OfertaBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_beneficio_oferta"))
    private Oferta oferta;

    @Column(name = "texto", nullable = false, length = 255)
    private String texto;

    @Column(name = "orden", nullable = false)
    private int orden;

    public OfertaBeneficio() {}

    public OfertaBeneficio(Oferta oferta, String texto, int orden) {
        this.oferta = oferta;
        this.texto = texto;
        this.orden = orden;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}
