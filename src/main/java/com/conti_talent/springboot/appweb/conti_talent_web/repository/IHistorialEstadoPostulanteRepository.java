package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.HistorialEstadoPostulante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IHistorialEstadoPostulanteRepository extends JpaRepository<HistorialEstadoPostulante, Long> {
    List<HistorialEstadoPostulante> findByPostulanteIdOrderByFechaCambioDesc(Long postulanteId);
}
