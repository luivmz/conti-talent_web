package com.conti_talent.springboot.appweb.conti_talent_web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Vistas del panel de administracion (carpeta templates/admin/).
 * Sin logica de negocio; los datos los pinta el JS modular consumiendo /api/*.
 */
@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping({"", "/", "/dashboard", "/dashboard.html"})
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping({"/areas", "/admin-areas.html"})
    public String areas() {
        return "admin/admin-areas";
    }

    @GetMapping({"/ofertas", "/admin-ofertas.html"})
    public String ofertas() {
        return "admin/admin-ofertas";
    }

    @GetMapping({"/postulantes", "/admin-postulantes.html"})
    public String postulantes() {
        return "admin/admin-postulantes";
    }

    @GetMapping({"/preguntas", "/admin-preguntas.html"})
    public String preguntas() {
        return "admin/admin-preguntas";
    }

    @GetMapping({"/usuarios", "/admin-usuarios.html"})
    public String usuarios() {
        return "admin/admin-usuarios";
    }

    @GetMapping({"/metricas", "/admin-metricas.html"})
    public String metricas() {
        return "admin/admin-metricas";
    }
}
