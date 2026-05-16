package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.model.DocumentoPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IDocumentoPostulanteService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoRestController {

    private final IDocumentoPostulanteService documentoService;

    public DocumentoRestController(IDocumentoPostulanteService documentoService) {
        this.documentoService = documentoService;
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargar(@PathVariable Long id) {
        DocumentoPostulante documento = documentoService.obtenerPorId(id);
        Resource resource = documentoService.cargarRecurso(documento);
        boolean esPdf = "pdf".equalsIgnoreCase(documento.getExtension());
        return ResponseEntity.ok()
                .contentType(esPdf ? MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        (esPdf ? "inline" : "attachment") + "; filename=\"" + documento.getNombreOriginal().replace("\"", "") + "\"")
                .body(resource);
    }
}
