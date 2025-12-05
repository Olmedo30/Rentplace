package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface usuarioDAO extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Nuevo método para buscar por rol
    List<Usuario> findByRol(Usuario.Rol rol);
}