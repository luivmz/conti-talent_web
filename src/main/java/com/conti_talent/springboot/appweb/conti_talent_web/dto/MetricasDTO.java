package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Estructura idéntica a la que el frontend hoy lee de localStorage en la clave
 * `metricas` (ver seed.js). Mantiene `series` y `estadoActual` con la misma forma.
 */
public class MetricasDTO {

    private Map<String, Serie> series;
    private EstadoActual estadoActual;

    public MetricasDTO() {
        this.series = new LinkedHashMap<>();
        this.estadoActual = new EstadoActual();
    }

    public Map<String, Serie> getSeries() { return series; }
    public void setSeries(Map<String, Serie> series) { this.series = series; }

    public EstadoActual getEstadoActual() { return estadoActual; }
    public void setEstadoActual(EstadoActual estadoActual) { this.estadoActual = estadoActual; }

    /* ---------------- Serie de tiempo ---------------- */
    public static class Serie {
        private String label;
        private String unidad;
        private List<Punto> puntos;

        public Serie() {}

        public Serie(String label, String unidad, List<Punto> puntos) {
            this.label = label;
            this.unidad = unidad;
            this.puntos = puntos;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public String getUnidad() { return unidad; }
        public void setUnidad(String unidad) { this.unidad = unidad; }

        public List<Punto> getPuntos() { return puntos; }
        public void setPuntos(List<Punto> puntos) { this.puntos = puntos; }
    }

    public static class Punto {
        private String mes;
        private double valor;

        public Punto() {}

        public Punto(String mes, double valor) {
            this.mes = mes;
            this.valor = valor;
        }

        public String getMes() { return mes; }
        public void setMes(String mes) { this.mes = mes; }

        public double getValor() { return valor; }
        public void setValor(double valor) { this.valor = valor; }
    }

    /* ---------------- KPIs puntuales ---------------- */
    public static class EstadoActual {
        private int postulantesActivos;
        private int ofertasAbiertas;
        private int entrevistasHoy;
        private int ofertasEsteMes;
        private String tiempoPromedio;

        public int getPostulantesActivos() { return postulantesActivos; }
        public void setPostulantesActivos(int postulantesActivos) { this.postulantesActivos = postulantesActivos; }

        public int getOfertasAbiertas() { return ofertasAbiertas; }
        public void setOfertasAbiertas(int ofertasAbiertas) { this.ofertasAbiertas = ofertasAbiertas; }

        public int getEntrevistasHoy() { return entrevistasHoy; }
        public void setEntrevistasHoy(int entrevistasHoy) { this.entrevistasHoy = entrevistasHoy; }

        public int getOfertasEsteMes() { return ofertasEsteMes; }
        public void setOfertasEsteMes(int ofertasEsteMes) { this.ofertasEsteMes = ofertasEsteMes; }

        public String getTiempoPromedio() { return tiempoPromedio; }
        public void setTiempoPromedio(String tiempoPromedio) { this.tiempoPromedio = tiempoPromedio; }
    }
}
