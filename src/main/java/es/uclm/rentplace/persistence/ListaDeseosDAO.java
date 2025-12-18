package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.ListaDeseos;
import es.uclm.rentplace.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListaDeseosDAO extends JpaRepository<ListaDeseos, Long> {
    Optional<ListaDeseos> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
}