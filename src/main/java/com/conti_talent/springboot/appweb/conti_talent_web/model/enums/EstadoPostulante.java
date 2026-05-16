package com.conti_talent.springboot.appweb.conti_talent_web.model.enums;

/**
 * @deprecated Reemplazado por {@link EstadoCodigo}. Se mantiene como alias
 * temporal para no romper imports antiguos durante la migracion. Sera
 * eliminado en una proxima version.
 */
@Deprecated
public enum EstadoPostulante {
    POSTULADO, EN_EVALUACION, APROBADO_TECNICO, ENTREVISTA,
    EVALUACION_PSICOLOGICA, ACEPTADO, RECHAZADO;

    /** Conversion de conveniencia hacia el nuevo enum. */
    public EstadoCodigo aCodigo() {
        return EstadoCodigo.valueOf(this.name());
    }

    /** Delegada al nuevo enum. */
    public boolean puedeTransicionarA(EstadoPostulante destino) {
        if (destino == null) return false;
        return this.aCodigo().puedeTransicionarA(destino.aCodigo());
    }
}
