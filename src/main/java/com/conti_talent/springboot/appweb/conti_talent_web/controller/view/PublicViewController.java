package com.conti_talent.springboot.appweb.conti_talent_web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC controller — vistas publicas servidas por Thymeleaf.
 * Solo retornan el nombre de la plantilla. Toda la logica vive en services
 * y se consume desde el JS modular existente via /api/*.
 *
 * Aceptamos tambien las rutas con sufijo .html para no romper los enlaces
 * actuales del frontend (ej. <a href="ofertas.html">).
 */
@Controller
public class PublicViewController {

    @GetMapping({"/", "/index", "/index.html"})
    public String home() {
        return "index";
    }

    @GetMapping({"/login", "/login.html"})
    public String login() {
        return "login";
    }

    @GetMapping({"/registro", "/registro.html"})
    public String registro() {
        return "registro";
    }

    @GetMapping({"/ofertas", "/ofertas.html"})
    public String ofertas() {
        return "ofertas";
    }

    @GetMapping({"/detalle-oferta", "/detalle-oferta.html"})
    public String detalleOferta() {
        return "detalle-oferta";
    }

    @GetMapping({"/postular", "/postular.html"})
    public String postular() {
        return "postular";
    }

    @GetMapping({"/evaluacion", "/evaluacion.html"})
    public String evaluacion() {
        return "evaluacion";
    }

    @GetMapping({"/areas", "/areas.html"})
    public String areas() {
        return "areas";
    }

    @GetMapping({"/contacto", "/contacto.html"})
    public String contacto() {
        return "contacto";
    }

    @GetMapping({"/publicidad", "/publicidad.html"})
    public String publicidad() {
        return "publicidad";
    }

    @GetMapping({"/mi-estado", "/mi-estado.html"})
    public String miEstado() {
        return "redirect:/mis-postulaciones.html";
    }

    @GetMapping({"/mi-proceso", "/mi-proceso.html"})
    public String miProceso() {
        return "mi-proceso";
    }

    @GetMapping({"/mis-postulaciones", "/mis-postulaciones.html"})
    public String misPostulaciones() {
        return "mis-postulaciones";
    }

    @GetMapping({"/mis-respuestas", "/mis-respuestas.html"})
    public String misRespuestas() {
        return "mis-respuestas";
    }
}
