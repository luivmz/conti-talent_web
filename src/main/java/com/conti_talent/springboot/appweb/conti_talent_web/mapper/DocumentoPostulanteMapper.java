package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.DocumentoPostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.DocumentoPostulante;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentoPostulanteMapper {

    public DocumentoPostulanteDTO convertirADTO(DocumentoPostulante documento) {
        if (documento == null) return null;
        DocumentoPostulanteDTO dto = new DocumentoPostulanteDTO();
        dto.setId(documento.getId());
        dto.setPostulanteId(documento.getPostulanteId());
        dto.setTipoDocumento(documento.getTipoDocumento());
        dto.setNombreOriginal(documento.getNombreOriginal());
        dto.setNombreArchivo(documento.getNombreArchivo());
        dto.setExtension(documento.getExtension());
        dto.setTamanio(documento.getTamanio());
        dto.setFechaSubida(documento.getFechaSubida());
        dto.setUrlDescarga("/api/postulantes/" + documento.getPostulanteId() + "/documentos/" + documento.getId() + "/descargar");
        return dto;
    }

    public List<DocumentoPostulanteDTO> convertirALista(List<DocumentoPostulante> documentos) {
        if (documentos == null) return List.of();
        return documentos.stream().map(this::convertirADTO).toList();
    }
}
