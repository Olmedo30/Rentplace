package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropietarioDAO extends JpaRepository<Propietario, Long> {

    // Buscar por email 
    Optional<Propietario> findByEmail(String email);
    boolean existsByEmail(String email);

    // Buscar por nombre 
    List<Propietario> findByNombreContainingIgnoreCase(String nombre);

    // El método por id ya lo proporciona JpaRepository: findById(id)
}
