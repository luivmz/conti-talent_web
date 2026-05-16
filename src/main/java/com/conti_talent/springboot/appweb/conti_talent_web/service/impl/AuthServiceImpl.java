package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import java.time.LocalDateTime;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.LoginRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.RegistroRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.SesionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.UnauthorizedException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.UsuarioMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.RolCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IRolRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IUsuarioRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioRepository usuarioRepository;
    private final IRolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;

    public AuthServiceImpl(IUsuarioRepository usuarioRepository,
                           IRolRepository rolRepository,
                           UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public SesionDTO login(LoginRequest request) {
        if (request == null
                || esTextoVacio(request.getEmail())
                || esTextoVacio(request.getPassword())) {
            throw new UnauthorizedException("Credenciales invalidas");
        }
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas o cuenta desactivada"));

        if (!usuario.isActivo() || !usuario.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Credenciales invalidas o cuenta desactivada");
        }
        return usuarioMapper.convertirASesion(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO register(RegistroRequest request) {
        if (request == null
                || esTextoVacio(request.getEmail())
                || esTextoVacio(request.getPassword())) {
            throw new BusinessException("Email y password son obligatorios");
        }
        if (usuarioRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BusinessException("Ya existe una cuenta con este correo");
        }
        Rol rolPostulante = rolRepository.findByCodigo(RolCodigo.POSTULANTE.name())
                .orElseThrow(() -> new BusinessException(
                        "No se encontro el rol POSTULANTE en el sistema"));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(textoSeguro(request.getNombre()));
        nuevoUsuario.setApellido(textoSeguro(request.getApellido()));
        nuevoUsuario.setEmail(request.getEmail().trim());
        nuevoUsuario.setPassword(request.getPassword());
        nuevoUsuario.setRol(rolPostulante);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setCreadoEn(LocalDateTime.now());

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        return usuarioMapper.convertirADTO(usuarioGuardado);
    }

    @Override
    public SesionDTO buildSession(UsuarioDTO usuario) {
        if (usuario == null) return null;
        String codigoRolMinuscula = usuario.getRol() != null && usuario.getRol().getCodigo() != null
                ? usuario.getRol().getCodigo().toLowerCase()
                : "";
        return new SesionDTO(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getEmail(), codigoRolMinuscula);
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private static String textoSeguro(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
