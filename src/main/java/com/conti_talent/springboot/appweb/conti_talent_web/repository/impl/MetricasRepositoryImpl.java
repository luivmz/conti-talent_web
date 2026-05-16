package com.conti_talent.springboot.appweb.conti_talent_web.repository.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IMetricasRepository;
import org.springframework.stereotype.Component;

/**
 * Impl in-memory del MetricasRepository (no JPA). Las metricas precomputadas
 * son un snapshot estatico cargado por el DataLoader.
 */
@Component
public class MetricasRepositoryImpl implements IMetricasRepository {

    private volatile MetricasDTO snapshot = new MetricasDTO();

    @Override
    public MetricasDTO load() {
        return snapshot;
    }

    @Override
    public void save(MetricasDTO metricas) {
        if (metricas != null) this.snapshot = metricas;
    }
}
