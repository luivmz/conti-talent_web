package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.response.RankingItemDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IMetricasService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metricas")
public class MetricasRestController {

    private final IMetricasService metricasService;

    public MetricasRestController(IMetricasService metricasService) {
        this.metricasService = metricasService;
    }

    @GetMapping
    public MetricasDTO dashboard() {
        return metricasService.obtenerDashboard();
    }

    @GetMapping("/ranking")
    public List<RankingItemDTO> ranking(@RequestParam(value = "oferta", required = false) Long ofertaId,
                                        @RequestParam(value = "limit",  defaultValue = "10") int limite) {
        return metricasService.ranking(ofertaId, limite);
    }

    @GetMapping("/por-estado")
    public Map<EstadoCodigo, Long> porEstado() {
        return metricasService.postulantesPorEstado();
    }

    @GetMapping("/ofertas-top")
    public List<Map<String, Object>> ofertasTop(@RequestParam(value = "limit", defaultValue = "5") int limite) {
        return metricasService.ofertasTop(limite);
    }
}
