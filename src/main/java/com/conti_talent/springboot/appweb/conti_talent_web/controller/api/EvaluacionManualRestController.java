package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EntrevistaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EntrevistaRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionPsicologicaRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEntrevistaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEvaluacionManualService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/postulantes/{postulanteId}")
public class EvaluacionManualRestController {

    private final IEvaluacionManualService evaluacionManualService;
    private final IEntrevistaService entrevistaService;

    public EvaluacionManualRestController(IEvaluacionManualService evaluacionManualService,
                                          IEntrevistaService entrevistaService) {
        this.evaluacionManualService = evaluacionManualService;
        this.entrevistaService = entrevistaService;
    }

    @GetMapping("/entrevistas")
    public List<EntrevistaDTO> listarEntrevistas(@PathVariable Long postulanteId) {
        return entrevistaService.listarPorPostulante(postulanteId);
    }

    @PostMapping("/entrevistas")
    public EntrevistaDTO registrarEntrevista(@PathVariable Long postulanteId,
                                             @RequestBody EntrevistaRequest request) {
        return entrevistaService.crear(postulanteId, request);
    }

    @PostMapping("/evaluaciones-psicologicas")
    public PostulanteDTO registrarEvaluacionPsicologica(@PathVariable Long postulanteId,
                                                        @RequestBody EvaluacionPsicologicaRequest request) {
        return evaluacionManualService.registrarEvaluacionPsicologica(postulanteId, request);
    }
}
