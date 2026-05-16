package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.DocumentoPostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.DocumentoPostulante;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IDocumentoPostulanteService {
    DocumentoPostulanteDTO subir(Long postulanteId, String tipoDocumento, MultipartFile archivo);
    List<DocumentoPostulanteDTO> listar(Long postulanteId);
    DocumentoPostulante obtenerDocumento(Long postulanteId, Long documentoId);
    DocumentoPostulante obtenerPorId(Long documentoId);
    Resource cargarRecurso(DocumentoPostulante documento);
}
