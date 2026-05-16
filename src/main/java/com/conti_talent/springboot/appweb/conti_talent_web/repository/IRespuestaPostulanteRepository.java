package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.RespuestaPostulante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a datos de la entidad intermedia RespuestaPostulante.
 */
public interface IRespuestaPostulanteRepository extends JpaRepository<RespuestaPostulante, Long> {

    List<RespuestaPostulante> findByPostulante_Id(Long postulanteId);

    void deleteByPostulante_Id(Long postulanteId);
}
