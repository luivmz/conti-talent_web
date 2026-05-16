package com.conti_talent.springboot.appweb.conti_talent_web.dto;

public class AreaDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private String color;

    public AreaDTO() {
    }

    public AreaDTO(Long id, String nombre, String descripcion, String icono, String color) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
        this.color = color;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
