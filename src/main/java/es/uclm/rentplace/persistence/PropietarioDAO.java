package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropietarioDAO extends JpaRepository<Propietario, Long> {

    // Buscar por email 
    Optional<Propietario> findByUsuario_Email(String email);
    boolean existsByUsuario_Email(String email);

    // Buscar por nombre 
    List<Propietario> findByUsuario_UsernameContainingIgnoreCase(String username);
    
    // Buscar propietario por el ID de usuario asociado
    Optional<Propietario> findByUsuarioId(Long userId);

    // El método por id ya lo proporciona JpaRepository: findById(id)
}