package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EstadoDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEstadoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
public class EstadoRestController {

    private final IEstadoService estadoService;

    public EstadoRestController(IEstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @GetMapping
    public List<EstadoDTO> listar(@RequestParam(value = "activos", required = false) Boolean soloActivos) {
        if (Boolean.TRUE.equals(soloActivos)) return estadoService.listarSoloActivos();
        return estadoService.listarTodos();
    }

    @GetMapping("/{id}")
    public EstadoDTO obtener(@PathVariable Long id) {
        return estadoService.obtenerPorId(id);
    }
}
