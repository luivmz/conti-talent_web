package com.conti_talent.springboot.appweb.conti_talent_web.model;

import jakarta.persistence.*;

/**
 * Entidad intermedia que materializa la relacion N:M conceptual
 * Oferta - Requisito como una relacion 1:N explicita.
 *
 * Cada fila representa un requisito unico de una oferta especifica.
 * Tabla: TBL_OFERTA_REQUISITO.
 */
@Entity
@Table(name = "tbl_oferta_requisito")
public class OfertaRequisito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_requisito_oferta"))
    private Oferta oferta;

    @Column(name = "texto", nullable = false, length = 255)
    private String texto;

    @Column(name = "orden", nullable = false)
    private int orden;

    public OfertaRequisito() {}

    public OfertaRequisito(Oferta oferta, String texto, int orden) {
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
