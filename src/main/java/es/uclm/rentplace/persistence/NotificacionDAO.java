package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Notificacion;
import es.uclm.rentplace.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionDAO extends JpaRepository<Notificacion, Long> {
    
    // Buscar notificaciones por usuario
    List<Notificacion> findByUsuarioIdOrderByFechaEnvioDesc(Long usuarioId);
    
    // Buscar notificaciones no leídas
    List<Notificacion> findByUsuarioIdAndLeidaFalse(Long usuarioId);
    
    // Contar notificaciones no leídas
    long countByUsuarioIdAndLeidaFalse(Long usuarioId);
}