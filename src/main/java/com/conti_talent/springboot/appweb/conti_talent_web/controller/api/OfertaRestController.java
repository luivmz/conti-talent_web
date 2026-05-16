package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.OfertaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IOfertaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaRestController {

    private final IOfertaService service;

    public OfertaRestController(IOfertaService service) {
        this.service = service;
    }

    /**
     * GET /api/ofertas              -> todas las ofertas
     * GET /api/ofertas?area=1       -> filtra por id de area
     * GET /api/ofertas?destacadas   -> solo destacadas
     */
    @GetMapping
    public List<OfertaDTO> listar(@RequestParam(value = "area", required = false) Long areaId,
                                  @RequestParam(value = "destacadas", required = false) Boolean destacadas) {
        if (Boolean.TRUE.equals(destacadas)) return service.destacadas();
        if (areaId != null) return service.listarPorArea(areaId);
        return service.listar();
    }

    @GetMapping("/{id}")
    public OfertaDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<OfertaDTO> crear(@RequestBody OfertaDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(body));
    }

    @PutMapping("/{id}")
    public OfertaDTO actualizar(@PathVariable Long id, @RequestBody OfertaDTO body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
