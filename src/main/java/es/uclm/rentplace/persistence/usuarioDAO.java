package es.uclm.rentplace.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.uclm.rentplace.entity.Usuario;

@Repository
public interface usuarioDAO extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}