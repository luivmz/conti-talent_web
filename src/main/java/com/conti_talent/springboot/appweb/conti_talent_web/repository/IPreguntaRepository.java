package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IPreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByOfertaId(Long ofertaId);
    @Transactional
    void deleteByOfertaId(Long ofertaId);
}
