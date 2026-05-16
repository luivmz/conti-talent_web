package com.conti_talent.springboot.appweb.conti_talent_web.dto.request;

/**
 * Cuerpo de creacion / actualizacion de usuario.
 * Acepta el rol como `rolId` (FK directa) o como `rolCodigo` (texto).
 */
public class UsuarioRequest {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Long rolId;
    private String rolCodigo;
    private Boolean activo;

    public UsuarioRequest() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }

    public String getRolCodigo() { return rolCodigo; }
    public void setRolCodigo(String rolCodigo) { this.rolCodigo = rolCodigo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
