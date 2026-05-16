package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import java.time.LocalDateTime;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.RolDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.RolMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IRolRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IRolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolServiceImpl implements IRolService {

    private final IRolRepository rolRepository;
    private final RolMapper rolMapper;

    public RolServiceImpl(IRolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listarTodos() {
        return rolMapper.convertirALista(rolRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listarSoloActivos() {
        return rolRepository.findAll().stream()
                .filter(Rol::isActivo)
                .map(rolMapper::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO obtenerPorId(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + id));
        return rolMapper.convertirADTO(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public Rol obtenerEntidadPorCodigo(String codigo) {
        return rolRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol con codigo '" + codigo + "' no encontrado"));
    }

    @Override
    @Transactional
    public RolDTO crear(RolDTO dto) {
        validarDatosBasicos(dto);
        if (rolRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Ya existe un rol con el codigo: " + dto.getCodigo());
        }
        Rol nuevoRol = new Rol();
        nuevoRol.setCodigo(dto.getCodigo().trim().toUpperCase());
        nuevoRol.setNombre(dto.getNombre().trim());
        nuevoRol.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : "");
        nuevoRol.setActivo(true);
        nuevoRol.setCreadoEn(LocalDateTime.now());
        return rolMapper.convertirADTO(rolRepository.save(nuevoRol));
    }

    @Override
    @Transactional
    public RolDTO actualizar(Long id, RolDTO dto) {
        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + id));
        if (dto.getNombre() != null)      rolExistente.setNombre(dto.getNombre().trim());
        if (dto.getDescripcion() != null) rolExistente.setDescripcion(dto.getDescripcion().trim());
        rolExistente.setActivo(dto.isActivo());
        return rolMapper.convertirADTO(rolRepository.save(rolExistente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rol no encontrado: " + id);
        }
        rolRepository.deleteById(id);
    }

    private void validarDatosBasicos(RolDTO dto) {
        if (dto == null) throw new BusinessException("Datos de rol requeridos");
        if (esTextoVacio(dto.getCodigo())) throw new BusinessException("El codigo del rol es obligatorio");
        if (esTextoVacio(dto.getNombre())) throw new BusinessException("El nombre del rol es obligatorio");
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
