package com.conti_talent.springboot.appweb.conti_talent_web.model.enums;

/**
 * @deprecated Reemplazado por {@link RolCodigo}. Se mantiene como alias
 * temporal para no romper imports antiguos durante la migracion. Sera
 * eliminado en una proxima version.
 */
@Deprecated
public enum Rol {
    ADMIN, POSTULANTE;

    /** Conversion de conveniencia hacia el nuevo enum. */
    public RolCodigo aCodigo() {
        return this == ADMIN ? RolCodigo.ADMIN : RolCodigo.POSTULANTE;
    }
}
