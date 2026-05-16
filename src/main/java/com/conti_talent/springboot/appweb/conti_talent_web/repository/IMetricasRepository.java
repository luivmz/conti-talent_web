package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;

/**
 * Repositorio de metricas precomputadas. NO se persiste en BD: vive en
 * memoria como snapshot recargado por el DataLoader. Las metricas dinamicas
 * (ranking, conteos por estado, top ofertas) se calculan on-the-fly desde
 * los repositorios JPA en MetricasServiceImpl.
 */
public interface IMetricasRepository {

    MetricasDTO load();

    void save(MetricasDTO metricas);
}
