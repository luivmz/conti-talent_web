package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.LoginRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.RegistroRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.SesionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.ApiResponse;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.UnauthorizedException;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth basado en HttpSession (sin Spring Security).
 * Contrato compatible con el frontend (auth.js / page-login.js):
 *  - login   -> ApiResponse<SesionDTO>
 *  - me      -> SesionDTO (200) o 401
 *  - logout  -> ApiResponse<Void>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    public static final String SESSION_ATTR = "session";

    private final IAuthService authService;

    public AuthRestController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<SesionDTO> login(@RequestBody LoginRequest body, HttpSession session) {
        SesionDTO sesion = authService.login(body);
        session.setAttribute(SESSION_ATTR, sesion);
        return ApiResponse.ok(sesion);
    }

    @PostMapping("/registro")
    public ApiResponse<SesionDTO> registro(@RequestBody RegistroRequest body, HttpSession session) {
        UsuarioDTO usuario = authService.register(body);
        SesionDTO sesion = authService.buildSession(usuario);
        session.setAttribute(SESSION_ATTR, sesion);
        return ApiResponse.ok(sesion);
    }

    @GetMapping("/me")
    public ResponseEntity<SesionDTO> me(HttpSession session) {
        SesionDTO sesion = (SesionDTO) session.getAttribute(SESSION_ATTR);
        if (sesion == null) {
            throw new UnauthorizedException("Necesitas autentificación");
        }
        return ResponseEntity.ok(sesion);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.removeAttribute(SESSION_ATTR);
        session.invalidate();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok());
    }
}
