package com.conti_talent.springboot.appweb.conti_talent_web.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Codigos de estado posibles en el flujo del postulante. Funciona como
 * espejo en Java de la tabla TBL_ESTADO en BD. Cada constante coincide
 * con la columna `codigo` del registro correspondiente.
 *
 * Flujo positivo:
 *   POSTULADO -> EN_EVALUACION -> APROBADO_TECNICO -> ENTREVISTA
 *             -> EVALUACION_PSICOLOGICA -> ACEPTADO
 *
 * En cualquier momento puede transicionar a RECHAZADO (estado terminal).
 */
public enum EstadoCodigo {

    POSTULADO,
    EN_EVALUACION,
    APROBADO_TECNICO,
    ENTREVISTA,
    EVALUACION_PSICOLOGICA,
    ACEPTADO,
    RECHAZADO;

    /** Orden canonico del flujo positivo (sin RECHAZADO). */
    private static final List<EstadoCodigo> FLUJO_POSITIVO = Arrays.asList(
            POSTULADO, EN_EVALUACION, APROBADO_TECNICO,
            ENTREVISTA, EVALUACION_PSICOLOGICA, ACEPTADO
    );

    /**
     * Indica si la transicion al estado destino es legal.
     * Reglas:
     *  - Cualquier estado no terminal puede ir a RECHAZADO.
     *  - El flujo positivo solo avanza un paso a la vez.
     *  - ACEPTADO y RECHAZADO son terminales.
     */
    public boolean puedeTransicionarA(EstadoCodigo destino) {
        if (destino == null) return false;
        if (this == destino) return true;
        if (this == ACEPTADO || this == RECHAZADO) return false;
        if (destino == RECHAZADO) return true;

        int indiceOrigen  = FLUJO_POSITIVO.indexOf(this);
        int indiceDestino = FLUJO_POSITIVO.indexOf(destino);
        if (indiceOrigen == -1 || indiceDestino == -1) return false;
        return indiceDestino == indiceOrigen + 1;
    }

    /** Indica si el estado es terminal (ACEPTADO o RECHAZADO). */
    public boolean esTerminal() {
        return this == ACEPTADO || this == RECHAZADO;
    }
}
