package com.conti_talent.springboot.appweb.conti_talent_web.dto.auth;

/**
 * Sesion devuelta al frontend tras login. El campo `rol` es el codigo
 * en minuscula ('admin'/'postulante') compatible con auth.js.
 */
public class SesionDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;

    public SesionDTO() {
    }

    public SesionDTO(Long id, String nombre, String apellido, String email, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
