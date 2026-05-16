package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EvaluacionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionRequest;

public interface IEvaluacionService {

    EvaluacionDTO calificar(EvaluacionRequest request);

    EvaluacionDTO obtenerPorPostulante(Long postulanteId);
}
