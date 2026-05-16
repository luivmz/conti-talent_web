package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.time.LocalDateTime;

public class EstadoDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private int orden;
    private boolean terminal;
    private boolean activo;
    private LocalDateTime creadoEn;

    public EstadoDTO() {
    }

    public EstadoDTO(Long id, String codigo, String nombre, String descripcion,
                     int orden, boolean terminal, boolean activo, LocalDateTime creadoEn) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.orden = orden;
        this.terminal = terminal;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public boolean isTerminal() { return terminal; }
    public void setTerminal(boolean terminal) { this.terminal = terminal; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
