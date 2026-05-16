package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EvaluacionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEvaluacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionRestController {

    private final IEvaluacionService service;

    public EvaluacionRestController(IEvaluacionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EvaluacionDTO> calificar(@RequestBody EvaluacionRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.calificar(body));
    }

    @GetMapping("/postulante/{postulanteId}")
    public EvaluacionDTO obtenerPorPostulante(@PathVariable Long postulanteId) {
        return service.obtenerPorPostulante(postulanteId);
    }
}
