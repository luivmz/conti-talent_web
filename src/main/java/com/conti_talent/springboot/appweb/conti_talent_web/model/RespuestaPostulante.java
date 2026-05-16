package com.conti_talent.springboot.appweb.conti_talent_web.model;

import jakarta.persistence.*;

/**
 * Entidad intermedia que materializa la relacion N:M conceptual
 * Postulante - Pregunta como una relacion 1:N explicita.
 *
 * Cada fila representa la respuesta que un postulante dio a una pregunta
 * especifica de la evaluacion tecnica de la oferta a la que postulo.
 *
 * Tabla: TBL_POSTULANTE_RESPUESTA. Indice unico por (postulante_id, pregunta_id)
 * para impedir respuestas duplicadas a la misma pregunta.
 *
 * NOTA tecnica: usamos el patron "doble columna" (preguntaId + relacion lazy)
 * para poder grabar respuestas conociendo solo el id de la pregunta sin tener
 * que cargar la entidad Pregunta dentro del setter del Postulante.
 */
@Entity
@Table(name = "tbl_postulante_respuesta",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_respuesta_postulante_pregunta",
                columnNames = {"postulante_id", "pregunta_id"}))
public class RespuestaPostulante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "postulante_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_respuesta_postulante"))
    private Postulante postulante;

    /**
     * Lectura lazy de la pregunta. La columna real (pregunta_id) la maneja
     * el campo {@link #preguntaId} de abajo, por eso esta relacion es read-only.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_respuesta_pregunta"))
    private Pregunta pregunta;

    @Column(name = "pregunta_id", nullable = false)
    private Long preguntaId;

    @Column(name = "opcion_elegida", nullable = false)
    private int opcionElegida;

    public RespuestaPostulante() {}

    public RespuestaPostulante(Postulante postulante, Long preguntaId, int opcionElegida) {
        this.postulante = postulante;
        this.preguntaId = preguntaId;
        this.opcionElegida = opcionElegida;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Postulante getPostulante() { return postulante; }
    public void setPostulante(Postulante postulante) { this.postulante = postulante; }

    public Pregunta getPregunta() { return pregunta; }

    public Long getPreguntaId() { return preguntaId; }
    public void setPreguntaId(Long preguntaId) { this.preguntaId = preguntaId; }

    public int getOpcionElegida() { return opcionElegida; }
    public void setOpcionElegida(int opcionElegida) { this.opcionElegida = opcionElegida; }
}
