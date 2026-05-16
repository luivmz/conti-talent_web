package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EvaluacionCompuestaService {

    public void recalcular(Postulante postulante) {
        postulante.setPuntajeExperiencia(calcularPuntajeExperiencia(postulante.getAniosExperiencia()));
        postulante.setPuntajeHabilidades(calcularPuntajeHabilidades(postulante.getOferta(), postulante.getHabilidades()));
        postulante.setPuntajeFinal(calcularPuntajeFinal(
                postulante.getPuntaje(),
                postulante.getPuntajeExperiencia(),
                postulante.getPuntajeHabilidades()));
    }

    public int calcularPuntajeExperiencia(int aniosExperiencia) {
        if (aniosExperiencia <= 0) return 0;
        if (aniosExperiencia == 1) return 30;
        if (aniosExperiencia == 2) return 60;
        return 100;
    }

    public int calcularPuntajeHabilidades(Oferta oferta, String habilidadesDeclaradas) {
        List<String> requeridas = oferta != null ? oferta.getHabilidadesRequeridas() : List.of();
        if (requeridas == null || requeridas.isEmpty()) return 0;
        Set<String> declaradas = separarHabilidades(habilidadesDeclaradas);
        if (declaradas.isEmpty()) return 0;
        long coincidencias = requeridas.stream()
                .map(this::normalizar)
                .filter(valor -> !valor.isBlank())
                .filter(declaradas::contains)
                .count();
        return (int) Math.round((coincidencias * 100.0) / requeridas.size());
    }

    public int calcularPuntajeFinal(int puntajeCuestionario, int puntajeExperiencia, int puntajeHabilidades) {
        return (int) Math.round((puntajeCuestionario * 0.50)
                + (puntajeExperiencia * 0.30)
                + (puntajeHabilidades * 0.20));
    }

    private Set<String> separarHabilidades(String habilidades) {
        if (habilidades == null) return Set.of();
        return Arrays.stream(habilidades.split("[,;\\n]"))
                .map(this::normalizar)
                .filter(valor -> !valor.isBlank())
                .collect(Collectors.toSet());
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.trim().toLowerCase(Locale.ROOT);
    }
}
