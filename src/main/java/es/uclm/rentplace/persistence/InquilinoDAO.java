package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Inquilino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InquilinoDAO extends JpaRepository<Inquilino, Long> {
    Optional<Inquilino> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
}