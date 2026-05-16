package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.RankingItemDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;

import java.util.List;
import java.util.Map;

public interface IMetricasService {

    MetricasDTO obtenerDashboard();

    List<RankingItemDTO> ranking(Long ofertaId, int limite);

    Map<EstadoCodigo, Long> postulantesPorEstado();

    List<Map<String, Object>> ofertasTop(int limite);
}
