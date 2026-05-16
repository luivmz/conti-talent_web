package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.EntrevistaPostulante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEntrevistaPostulanteRepository extends JpaRepository<EntrevistaPostulante, Long> {
    List<EntrevistaPostulante> findByPostulante_IdOrderByFechaProgramadaDesc(Long postulanteId);
    boolean existsByPostulante_IdAndTipoEntrevistaAndEstadoEntrevistaIn(
            Long postulanteId, String tipoEntrevista, List<String> estados);
    boolean existsByPostulante_IdAndTipoEntrevistaAndResultado(Long postulanteId, String tipoEntrevista, String resultado);
}
