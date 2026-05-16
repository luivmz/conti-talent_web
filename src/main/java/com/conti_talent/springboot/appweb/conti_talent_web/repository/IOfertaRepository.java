package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOfertaRepository extends JpaRepository<Oferta, Long> {
    List<Oferta> findByAreaId(Long areaId);
    List<Oferta> findByDestacadaTrue();

    default List<Oferta> findFeatured() { return findByDestacadaTrue(); }
}
