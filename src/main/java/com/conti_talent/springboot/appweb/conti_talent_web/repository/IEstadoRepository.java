package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IEstadoRepository extends JpaRepository<Estado, Long> {
    Optional<Estado> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    default List<Estado> listarTodos() { return findAll(); }
    default Optional<Estado> buscarPorId(Long id) { return findById(id); }
    default Optional<Estado> buscarPorCodigo(String codigo) { return findByCodigo(codigo); }
    default Estado guardar(Estado estado) { return save(estado); }
    default void eliminarPorId(Long id) { deleteById(id); }
    default boolean existePorCodigo(String codigo) { return existsByCodigo(codigo); }
}
