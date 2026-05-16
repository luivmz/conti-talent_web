package com.conti_talent.springboot.appweb.conti_talent_web.controller.api;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.PostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostulanteAdminRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.PostularRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.model.DocumentoPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IDocumentoPostulanteService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IPostulanteService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/postulantes")
public class PostulanteRestController {

    private final IPostulanteService postulanteService;
    private final IDocumentoPostulanteService documentoService;

    public PostulanteRestController(IPostulanteService postulanteService,
                                    IDocumentoPostulanteService documentoService) {
        this.postulanteService = postulanteService;
        this.documentoService = documentoService;
    }

    /**
     * GET /api/postulantes                -> todos
     * GET /api/postulantes?oferta=1       -> por oferta
     * GET /api/postulantes?usuario=2      -> por usuario
     */
    @GetMapping
    public List<PostulanteDTO> listar(@RequestParam(value = "oferta",  required = false) Long ofertaId,
                                      @RequestParam(value = "usuario", required = false) Long usuarioId) {
        if (ofertaId  != null) return postulanteService.listarPorOferta(ofertaId);
        if (usuarioId != null) return postulanteService.listarPorUsuario(usuarioId);
        return postulanteService.listarTodos();
    }

    @GetMapping("/{id}")
    public PostulanteDTO obtener(@PathVariable Long id) {
        return postulanteService.obtenerPorId(id);
    }

    @GetMapping("/{id}/proceso")
    public PostulanteDTO obtenerProceso(@PathVariable Long id) {
        return postulanteService.obtenerPorId(id);
    }

    @GetMapping("/ranking")
    public List<PostulanteDTO> ranking(@RequestParam(value = "oferta", required = false) Long ofertaId,
                                       @RequestParam(value = "estado", required = false) String estado,
                                       @RequestParam(value = "area", required = false) Long areaId) {
        return postulanteService.obtenerRanking(ofertaId, estado, areaId);
    }

    @PostMapping
    public ResponseEntity<PostulanteDTO> postular(@RequestBody PostularRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postulanteService.registrarPostulacion(body));
    }

    @PutMapping("/{id}")
    public PostulanteDTO actualizar(@PathVariable Long id, @RequestBody PostulanteAdminRequest body) {
        return postulanteService.actualizarDesdeAdmin(id, body);
    }

    /**
     * PATCH /api/postulantes/{id}/estado
     * Body: { "estado": "ENTREVISTA" }   <- codigo logico
     *   o:  { "estadoId": "4" }          <- id numerico de tabla
     */
    @PatchMapping("/{id}/estado")
    public PostulanteDTO cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String referenciaEstado = body.containsKey("estadoId") ? body.get("estadoId") : body.get("estado");
        return postulanteService.cambiarEstado(id, referenciaEstado, body.get("usuarioAdmin"),
                body.get("observacionInterna"), body.get("observacionPostulante"));
    }

    @PatchMapping("/{id}/observaciones")
    public PostulanteDTO actualizarObservacion(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return postulanteService.actualizarObservacionAdmin(id, body.get("observacionAdmin"));
    }

    @PostMapping(value = "/{id}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirDocumento(@PathVariable Long id,
                                            @RequestParam("tipoDocumento") String tipoDocumento,
                                            @RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoService.subir(id, tipoDocumento, archivo));
    }

    @GetMapping("/{id}/documentos")
    public List<?> listarDocumentos(@PathVariable Long id) {
        return documentoService.listar(id);
    }

    @GetMapping("/{id}/documentos/{documentoId}/descargar")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id, @PathVariable Long documentoId) {
        DocumentoPostulante documento = documentoService.obtenerDocumento(id, documentoId);
        Resource resource = documentoService.cargarRecurso(documento);
        boolean esPdf = "pdf".equalsIgnoreCase(documento.getExtension());
        return ResponseEntity.ok()
                .contentType(esPdf ? MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        (esPdf ? "inline" : "attachment") + "; filename=\"" + documento.getNombreOriginal().replace("\"", "") + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        postulanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Eliminacion logica: marca al postulante como RECHAZADO. */
    @PostMapping("/{id}/rechazar")
    public PostulanteDTO rechazar(@PathVariable Long id) {
        return postulanteService.marcarComoRechazado(id);
    }
}
