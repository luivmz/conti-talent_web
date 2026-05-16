package com.conti_talent.springboot.appweb.conti_talent_web.repository;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Usuario> findByRolId(Long rolId);

    default Optional<Usuario> findByEmail(String email) { return findByEmailIgnoreCase(email); }
    default boolean existsByEmail(String email) { return existsByEmailIgnoreCase(email); }
}
