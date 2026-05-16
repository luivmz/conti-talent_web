package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.RolDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IRolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolRestController {

    private final IRolService rolService;

    public RolRestController(IRolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<RolDTO> listar(@RequestParam(value = "activos", required = false) Boolean soloActivos) {
        if (Boolean.TRUE.equals(soloActivos)) return rolService.listarSoloActivos();
        return rolService.listarTodos();
    }

    @GetMapping("/{id}")
    public RolDTO obtener(@PathVariable Long id) {
        return rolService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<RolDTO> crear(@RequestBody RolDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.crear(body));
    }

    @PutMapping("/{id}")
    public RolDTO actualizar(@PathVariable Long id, @RequestBody RolDTO body) {
        return rolService.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
