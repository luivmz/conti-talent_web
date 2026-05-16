package com.conti_talent.springboot.appweb.conti_talent_web.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Codigos de rol soportados en la plataforma. Funciona como espejo en Java
 * de la tabla de roles que vive en BD: cada constante coincide con la columna
 * `codigo` de la tabla TBL_ROL. Esto permite mantener validacion en
 * compile-time mientras la persistencia real es por FK.
 *
 * Se serializa en minuscula ('admin', 'postulante') para mantener compatibilidad
 * con el frontend existente (auth.js y page-login.js).
 */
public enum RolCodigo {

    ADMIN("admin"),
    POSTULANTE("postulante");

    private final String valor;

    RolCodigo(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String obtenerValor() {
        return valor;
    }

    @JsonCreator
    public static RolCodigo desdeTexto(String texto) {
        if (texto == null) return POSTULANTE;
        for (RolCodigo codigo : values()) {
            if (codigo.valor.equalsIgnoreCase(texto) || codigo.name().equalsIgnoreCase(texto)) {
                return codigo;
            }
        }
        return POSTULANTE;
    }
}
