package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.RolDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.UsuarioDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.auth.SesionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de Usuario. La entidad ya trae el Rol por relacion JPA, asi que no
 * se hace lookup adicional. La password nunca se expone.
 */
@Component
public class UsuarioMapper {

    private final RolMapper rolMapper;

    public UsuarioMapper(RolMapper rolMapper) {
        this.rolMapper = rolMapper;
    }

    public UsuarioDTO convertirADTO(Usuario usuario) {
        if (usuario == null) return null;
        RolDTO rolEmbebido = rolMapper.convertirADTO(usuario.getRol());
        return new UsuarioDTO(
                usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRolId(), rolEmbebido,
                usuario.isActivo(), usuario.getCreadoEn());
    }

    public List<UsuarioDTO> convertirALista(List<Usuario> usuarios) {
        if (usuarios == null) return List.of();
        return usuarios.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    /** Construye SesionDTO. El codigo del rol se expone en minuscula. */
    public SesionDTO convertirASesion(Usuario usuario) {
        if (usuario == null) return null;
        String codigoRolMinuscula = usuario.getRol() != null && usuario.getRol().getCodigo() != null
                ? usuario.getRol().getCodigo().toLowerCase()
                : "";
        return new SesionDTO(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getEmail(), codigoRolMinuscula);
    }
}
