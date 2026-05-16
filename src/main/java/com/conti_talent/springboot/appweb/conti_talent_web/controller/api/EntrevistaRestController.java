package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EntrevistaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EntrevistaRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEntrevistaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entrevistas")
public class EntrevistaRestController {

    private final IEntrevistaService entrevistaService;

    public EntrevistaRestController(IEntrevistaService entrevistaService) {
        this.entrevistaService = entrevistaService;
    }

    @PutMapping("/{id}")
    public EntrevistaDTO actualizar(@PathVariable Long id, @RequestBody EntrevistaRequest request) {
        return entrevistaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/resultado")
    public PostulanteDTO registrarResultado(@PathVariable Long id, @RequestBody EntrevistaRequest request) {
        return entrevistaService.registrarResultado(id, request);
    }

    @PatchMapping("/{id}/cancelar")
    public EntrevistaDTO cancelar(@PathVariable Long id, @RequestBody(required = false) EntrevistaRequest request) {
        return entrevistaService.cancelar(id, request);
    }

    @PatchMapping("/{id}/reprogramar")
    public EntrevistaDTO reprogramar(@PathVariable Long id, @RequestBody EntrevistaRequest request) {
        return entrevistaService.reprogramar(id, request);
    }
}
