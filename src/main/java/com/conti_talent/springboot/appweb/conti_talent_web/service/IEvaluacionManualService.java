package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EntrevistaRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionPsicologicaRequest;

public interface IEvaluacionManualService {
    PostulanteDTO registrarEntrevista(Long postulanteId, EntrevistaRequest request);
    PostulanteDTO registrarEvaluacionPsicologica(Long postulanteId, EvaluacionPsicologicaRequest request);
}
