package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropietarioDAO extends JpaRepository<Propietario, Long> {
    Optional<Propietario> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
}