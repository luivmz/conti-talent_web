package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import java.time.LocalDateTime;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.UsuarioRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.UsuarioMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.RolCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IRolRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IUsuarioRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IUsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final IRolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(IUsuarioRepository usuarioRepository,
                              IRolRepository rolRepository,
                              UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listar() {
        return usuarioMapper.convertirALista(usuarioRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtener(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        return usuarioMapper.convertirADTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO crear(UsuarioRequest request) {
        validarDatosNuevoUsuario(request);
        if (usuarioRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BusinessException("Ya existe un usuario con ese correo");
        }
        Rol rolAsignado = resolverRolDesdeRequest(request);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(request.getNombre().trim());
        nuevoUsuario.setApellido(request.getApellido() != null ? request.getApellido().trim() : "");
        nuevoUsuario.setEmail(request.getEmail().trim());
        nuevoUsuario.setPassword(request.getPassword());
        nuevoUsuario.setRol(rolAsignado);
        nuevoUsuario.setActivo(request.getActivo() == null || request.getActivo());
        nuevoUsuario.setCreadoEn(LocalDateTime.now());
        return usuarioMapper.convertirADTO(usuarioRepository.save(nuevoUsuario));
    }

    @Override
    @Transactional
    public UsuarioDTO actualizar(Long id, UsuarioRequest request) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));

        if (request.getNombre()   != null) usuarioExistente.setNombre(request.getNombre().trim());
        if (request.getApellido() != null) usuarioExistente.setApellido(request.getApellido().trim());
        if (request.getEmail()    != null) {
            String emailNuevo = request.getEmail().trim();
            if (!emailNuevo.equalsIgnoreCase(usuarioExistente.getEmail())
                    && usuarioRepository.existsByEmailIgnoreCase(emailNuevo)) {
                throw new BusinessException("Ya existe un usuario con ese correo");
            }
            usuarioExistente.setEmail(emailNuevo);
        }
        if (esTextoNoVacio(request.getPassword())) {
            usuarioExistente.setPassword(request.getPassword());
        }
        if (request.getRolId() != null || esTextoNoVacio(request.getRolCodigo())) {
            Rol rolNuevo = resolverRolDesdeRequest(request);
            usuarioExistente.setRol(rolNuevo);
        }
        if (request.getActivo() != null) usuarioExistente.setActivo(request.getActivo());

        return usuarioMapper.convertirADTO(usuarioRepository.save(usuarioExistente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    /* ============ Helpers privados ============ */

    private static void validarDatosNuevoUsuario(UsuarioRequest request) {
        if (request == null
                || esTextoVacio(request.getNombre())
                || esTextoVacio(request.getEmail())
                || esTextoVacio(request.getPassword())) {
            throw new BusinessException("Nombre, email y password son obligatorios");
        }
    }

    private Rol resolverRolDesdeRequest(UsuarioRequest request) {
        if (request.getRolId() != null) {
            return rolRepository.findById(request.getRolId())
                    .orElseThrow(() -> new BusinessException("Rol no encontrado: " + request.getRolId()));
        }
        if (esTextoNoVacio(request.getRolCodigo())) {
            return rolRepository.findByCodigo(request.getRolCodigo().trim().toUpperCase())
                    .orElseThrow(() -> new BusinessException(
                            "Rol con codigo '" + request.getRolCodigo() + "' no existe"));
        }
        return rolRepository.findByCodigo(RolCodigo.POSTULANTE.name())
                .orElseThrow(() -> new BusinessException("No se encontro el rol POSTULANTE por defecto"));
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private static boolean esTextoNoVacio(String texto) {
        return !esTextoVacio(texto);
    }
}
