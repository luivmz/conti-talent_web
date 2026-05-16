package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.DocumentoPostulanteDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.mapper.DocumentoPostulanteMapper;
import com.conti_talent.springboot.appweb.conti_talent_web.model.DocumentoPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IDocumentoPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IDocumentoPostulanteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentoPostulanteServiceImpl implements IDocumentoPostulanteService {

    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("pdf", "docx", "png", "jpg", "jpeg");

    private final IDocumentoPostulanteRepository documentoRepository;
    private final IPostulanteRepository postulanteRepository;
    private final DocumentoPostulanteMapper documentoMapper;
    private final Path uploadsDir;
    private final Path cvDir;
    private final Path certificadosDir;
    private final long maxFileSizeBytes;

    public DocumentoPostulanteServiceImpl(IDocumentoPostulanteRepository documentoRepository,
                                          IPostulanteRepository postulanteRepository,
                                          DocumentoPostulanteMapper documentoMapper,
                                          @Value("${conti.uploads.postulantes-dir:uploads}") String uploadsDir,
                                          @Value("${conti.uploads.max-file-size-bytes:5242880}") long maxFileSizeBytes) {
        this.documentoRepository = documentoRepository;
        this.postulanteRepository = postulanteRepository;
        this.documentoMapper = documentoMapper;
        this.uploadsDir = Paths.get(uploadsDir).toAbsolutePath().normalize();
        this.cvDir = this.uploadsDir.resolve("cv").normalize();
        this.certificadosDir = this.uploadsDir.resolve("certificados").normalize();
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    @Override
    @Transactional
    public DocumentoPostulanteDTO subir(Long postulanteId, String tipoDocumento, MultipartFile archivo) {
        validarArchivo(archivo);
        Postulante postulante = postulanteRepository.findById(postulanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Postulante no encontrado: " + postulanteId));

        String original = StringUtils.cleanPath(archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "documento");
        String extension = obtenerExtension(original);
        String tipoNormalizado = normalizarTipo(tipoDocumento);
        String nombreUnico = postulanteId + "_" + UUID.randomUUID() + "." + extension;
        Path carpetaDestino = carpetaPorTipo(tipoNormalizado);

        try {
            Files.createDirectories(cvDir);
            Files.createDirectories(certificadosDir);
            Path destino = carpetaDestino.resolve(nombreUnico).normalize();
            if (!destino.startsWith(carpetaDestino)) {
                throw new BusinessException("Ruta de archivo invalida");
            }
            archivo.transferTo(destino);

            DocumentoPostulante documento = new DocumentoPostulante();
            documento.setPostulante(postulante);
            documento.setTipoDocumento(tipoNormalizado);
            documento.setNombreOriginal(original);
            documento.setNombreArchivo(nombreUnico);
            documento.setRutaArchivo(destino.toString());
            documento.setExtension(extension);
            documento.setTamanio(archivo.getSize());
            documento.setFechaSubida(LocalDateTime.now());
            DocumentoPostulante guardado = documentoRepository.save(documento);

            if ("CV".equals(guardado.getTipoDocumento())) {
                postulante.setCv(guardado.getNombreArchivo());
                postulanteRepository.save(postulante);
            }
            return documentoMapper.convertirADTO(guardado);
        } catch (IOException e) {
            throw new BusinessException("No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<DocumentoPostulanteDTO> listar(Long postulanteId) {
        return documentoMapper.convertirALista(documentoRepository.findByPostulanteIdOrderByFechaSubidaDesc(postulanteId));
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoPostulante obtenerDocumento(Long postulanteId, Long documentoId) {
        DocumentoPostulante documento = obtenerPorId(documentoId);
        if (!postulanteId.equals(documento.getPostulanteId())) {
            throw new ResourceNotFoundException("Documento no encontrado para el postulante indicado");
        }
        return documento;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoPostulante obtenerPorId(Long documentoId) {
        return documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado: " + documentoId));
    }

    @Override
    public Resource cargarRecurso(DocumentoPostulante documento) {
        FileSystemResource resource = new FileSystemResource(documento.getRutaArchivo());
        if (!resource.exists() || !resource.isReadable()) {
            throw new ResourceNotFoundException("Archivo fisico no encontrado");
        }
        return resource;
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) throw new BusinessException("Archivo requerido");
        if (archivo.getSize() > maxFileSizeBytes) throw new BusinessException("El archivo supera el tamanio maximo permitido");
        String extension = obtenerExtension(archivo.getOriginalFilename());
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new BusinessException("Extension no permitida. Permitidas: PDF, DOCX, PNG, JPG");
        }
    }

    private String obtenerExtension(String nombre) {
        if (nombre == null || !nombre.contains(".")) throw new BusinessException("Archivo sin extension");
        return nombre.substring(nombre.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String normalizarTipo(String tipoDocumento) {
        if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) return "OTRO";
        String normalizado = tipoDocumento.trim().toUpperCase(Locale.ROOT);
        return "ADICIONAL".equals(normalizado) ? "OTRO" : normalizado;
    }

    private Path carpetaPorTipo(String tipoDocumento) {
        if ("CV".equals(tipoDocumento)) return cvDir;
        return certificadosDir;
    }
}
