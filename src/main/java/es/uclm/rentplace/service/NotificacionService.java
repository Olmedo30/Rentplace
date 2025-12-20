package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Notificacion;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.NotificacionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {
    
    @Autowired
    private NotificacionDAO notificacionDAO;
    
    public List<Notificacion> obtenerNotificacionesDeUsuario(Long usuarioId) {
        return notificacionDAO.findByUsuarioIdOrderByFechaEnvioDesc(usuarioId);
    }
    
    public List<Notificacion> obtenerNotificacionesNoLeidas(Long usuarioId) {
        return notificacionDAO.findByUsuarioIdAndLeidaFalse(usuarioId);
    }
    

    public void crearNotificacion(Usuario usuario, String titulo, String mensaje, String tipo) {
    	Notificacion notificacion = new Notificacion(usuario, titulo, mensaje, tipo);
    	notificacionDAO.save(notificacion);
    }
    
    public long contarNotificacionesNoLeidas(Long usuarioId) {
        return notificacionDAO.countByUsuarioIdAndLeidaFalse(usuarioId);
    }
    
    public void marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificacionDAO.findById(notificacionId).orElse(null);
        if (notificacion != null) {
            notificacion.marcarComoLeida();
            notificacionDAO.save(notificacion);
        }
    }
    
    public void marcarTodasComoLeidas(Long usuarioId) {
        List<Notificacion> notificaciones = notificacionDAO.findByUsuarioIdAndLeidaFalse(usuarioId);
        for (Notificacion notificacion : notificaciones) {
            notificacion.marcarComoLeida();
        }
        notificacionDAO.saveAll(notificaciones);
    }
    
    public void crearNotificacion(Notificacion notificacion) {
        notificacionDAO.save(notificacion);
    }
}