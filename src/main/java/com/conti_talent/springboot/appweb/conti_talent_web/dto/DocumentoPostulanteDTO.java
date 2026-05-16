package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.time.LocalDateTime;

public class DocumentoPostulanteDTO {
    private Long id;
    private Long postulanteId;
    private String tipoDocumento;
    private String nombreOriginal;
    private String nombreArchivo;
    private String extension;
    private long tamanio;
    private LocalDateTime fechaSubida;
    private String urlDescarga;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostulanteId() { return postulanteId; }
    public void setPostulanteId(Long postulanteId) { this.postulanteId = postulanteId; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNombreOriginal() { return nombreOriginal; }
    public void setNombreOriginal(String nombreOriginal) { this.nombreOriginal = nombreOriginal; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public long getTamanio() { return tamanio; }
    public void setTamanio(long tamanio) { this.tamanio = tamanio; }

    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }

    public String getUrlDescarga() { return urlDescarga; }
    public void setUrlDescarga(String urlDescarga) { this.urlDescarga = urlDescarga; }
}
