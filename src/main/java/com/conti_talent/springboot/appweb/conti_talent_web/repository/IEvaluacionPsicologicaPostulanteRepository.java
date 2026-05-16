package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.EvaluacionPsicologicaPostulante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEvaluacionPsicologicaPostulanteRepository extends JpaRepository<EvaluacionPsicologicaPostulante, Long> {
    List<EvaluacionPsicologicaPostulante> findByPostulanteIdOrderByFechaEvaluacionDesc(Long postulanteId);
}
