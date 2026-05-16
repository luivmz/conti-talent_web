package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostulanteAdminRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;

import java.util.List;

public interface IPostulanteService {

    List<PostulanteDTO> listarTodos();

    PostulanteDTO obtenerPorId(Long id);

    List<PostulanteDTO> listarPorOferta(Long ofertaId);

    List<PostulanteDTO> listarPorUsuario(Long usuarioId);

    PostulanteDTO registrarPostulacion(PostularRequest request);

    PostulanteDTO actualizarDesdeAdmin(Long idPostulante, PostulanteAdminRequest request);

    PostulanteDTO cambiarEstado(Long idPostulante, String estadoDestino);

    /** Cambia el estado por id o por codigo logico (POSTULADO, ENTREVISTA, ...). */
    PostulanteDTO cambiarEstado(Long idPostulante, String estadoDestino, String usuarioAdmin,
                                 String observacionInterna, String observacionPostulante);

    PostulanteDTO actualizarObservacionAdmin(Long idPostulante, String observacionAdmin);

    void eliminar(Long id);

    PostulanteDTO marcarComoRechazado(Long id);

    List<PostulanteDTO> obtenerRankingPorPuntaje(Long ofertaId);

    List<PostulanteDTO> obtenerRanking(Long ofertaId, String estado, Long areaId);
}
