// src/main/java/es/uclm/rentplace/service/ReservaService.java
package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.SolicitudReserva;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.ReservaDAO;
import es.uclm.rentplace.persistence.SolicitudReservaDAO;
import es.uclm.rentplace.service.PropiedadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {
    
    @Autowired
    private ReservaDAO reservaDAO;
    
    @Autowired
    private PropiedadService propiedadService;
    
    @Autowired
    private SolicitudReservaDAO solicitudReservaDAO;
    
    // Método para confirmar una solicitud de reserva
    @Transactional
    public boolean confirmarSolicitudReserva(Long solicitudId) {
        SolicitudReserva solicitud = solicitudReservaDAO.findById(solicitudId).orElse(null);
        if (solicitud != null) {
            solicitud.setConfirmada(true);
            solicitudReservaDAO.save(solicitud);
            
            Reserva reserva = solicitud.getReserva();
            if (reserva != null) {
                reserva.setReservaConfirmada(true);
                reservaDAO.save(reserva);
                return true;
            }
        }
        return false;
    }
    
    // Método para rechazar una solicitud de reserva
    @Transactional
    public boolean rechazarSolicitudReserva(Long solicitudId) {
        SolicitudReserva solicitud = solicitudReservaDAO.findById(solicitudId).orElse(null);
        if (solicitud != null) {
            solicitud.setConfirmada(false);
            solicitudReservaDAO.save(solicitud);
            
            Reserva reserva = solicitud.getReserva();
            if (reserva != null) {
                reserva.setReservaConfirmada(false);
                reservaDAO.save(reserva);
                return true;
            }
        }
        return false;
    }
    
    // Método para encontrar reservas solapadas
    public List<Reserva> findSolapadas(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
        return reservaDAO.findSolapadas(propiedadId, fechaEntrada, fechaSalida);
    }
    
    // Método para obtener una reserva por ID
    public Reserva obtenerReservaPorId(Long id) {
        return reservaDAO.findById(id).orElse(null);
    }
    
    // Método para obtener reservas de un inquilino
    public List<Reserva> obtenerReservasDelinquino(Long inquilinoId) {
        return reservaDAO.findByInquilinoId(inquilinoId);
    }
    
    // Método para confirmar una reserva
    @Transactional
    public void confirmarReserva(Long reservaId) {
        Reserva reserva = reservaDAO.findById(reservaId).orElse(null);
        if (reserva != null) {
            reserva.setReservaConfirmada(true);
            reservaDAO.save(reserva);
        }
    }
    
    // Método para crear una reserva
    @Transactional
    public Reserva crearReserva(Usuario inquilino, Long propiedadId, LocalDateTime fechaEntrada, 
                               LocalDateTime fechaSalida, Reserva.PoliticaCancelacion politicaCancelacion) {
    	Propiedad propiedad = propiedadService.obtenerPorId(propiedadId);
    	if (propiedad == null) {
            throw new IllegalArgumentException("La propiedad no existe");
        }
    	Reserva reserva = new Reserva(inquilino, propiedad, fechaEntrada, fechaSalida, politicaCancelacion);
    	return reservaDAO.save(reserva);
    }
}