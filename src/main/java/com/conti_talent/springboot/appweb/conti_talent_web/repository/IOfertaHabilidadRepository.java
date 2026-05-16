package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.OfertaHabilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a datos de la entidad intermedia OfertaHabilidad.
 */
public interface IOfertaHabilidadRepository extends JpaRepository<OfertaHabilidad, Long> {

    List<OfertaHabilidad> findByOferta_IdOrderByOrdenAsc(Long ofertaId);

    void deleteByOferta_Id(Long ofertaId);
}
