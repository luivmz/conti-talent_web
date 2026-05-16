package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.LoginRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.RegistroRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.SesionDTO;

/**
 * Contrato del servicio de autenticacion. No usa Spring Security; la sesion
 * se persiste via HttpSession en el controller.
 */
public interface IAuthService {

    /** Valida credenciales y retorna la sesion. Lanza UnauthorizedException. */
    SesionDTO login(LoginRequest request);

    /** Registra un nuevo postulante (rol POSTULANTE asignado automaticamente). */
    UsuarioDTO register(RegistroRequest request);

    /** Construye la sesion para un usuario ya autenticado. */
    SesionDTO buildSession(UsuarioDTO usuario);
}
