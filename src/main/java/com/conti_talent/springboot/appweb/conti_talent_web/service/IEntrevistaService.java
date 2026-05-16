package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EntrevistaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EntrevistaRequest;

import java.util.List;

public interface IEntrevistaService {
    List<EntrevistaDTO> listarPorPostulante(Long postulanteId);
    EntrevistaDTO crear(Long postulanteId, EntrevistaRequest request);
    EntrevistaDTO actualizar(Long entrevistaId, EntrevistaRequest request);
    PostulanteDTO registrarResultado(Long entrevistaId, EntrevistaRequest request);
    EntrevistaDTO cancelar(Long entrevistaId, EntrevistaRequest request);
    EntrevistaDTO reprogramar(Long entrevistaId, EntrevistaRequest request);
}
