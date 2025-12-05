package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.*;
import es.uclm.rentplace.persistence.ReservaDAO;
import es.uclm.rentplace.persistence.SolicitudReservaDAO;
import es.uclm.rentplace.persistence.PagoDAO;
import es.uclm.rentplace.persistence.NotificacionDAO;

import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReservaService {
    
    private final ReservaDAO reservaDAO;
    private final SolicitudReservaDAO solicitudReservaDAO;
    private final PagoDAO pagoDAO;
    private final NotificacionDAO notificacionDAO;
    private final PropiedadService propiedadService;
    private final NotificacionService notificacionService;
    
    @Lazy
    @Autowired
    private PagoService pagoService;
    
    @Autowired
    public ReservaService(ReservaDAO reservaDAO, 
                         SolicitudReservaDAO solicitudReservaDAO,
                         PagoDAO pagoDAO,
                         NotificacionDAO notificacionDAO,
                         PropiedadService propiedadService,
                         NotificacionService notificacionService) {
        this.reservaDAO = reservaDAO;
        this.solicitudReservaDAO = solicitudReservaDAO;
        this.pagoDAO = pagoDAO;
        this.notificacionDAO = notificacionDAO;
        this.propiedadService = propiedadService;
        this.notificacionService = notificacionService;
        this.pagoService = pagoService;
    }
    
    @Transactional
    public Reserva crearReserva(Usuario inquilino, Long propiedadId, LocalDateTime fechaEntrada, 
                               LocalDateTime fechaSalida, Reserva.PoliticaCancelacion politicaCancelacion) {
        // Verificar disponibilidad sin usar DisponibilidadService
        Propiedad propiedad = propiedadService.obtenerPorId(propiedadId);
        if (propiedad == null) return null;
        
        // Verificar disponibilidad usando el DAO directamente
        List<Reserva> reservasSolapadas = reservaDAO.findSolapadas(
            propiedadId, fechaEntrada, fechaSalida
        );
        
        if (!reservasSolapadas.isEmpty()) {
            return null; // No disponible
        }
        
        // Calcular monto total
        long dias = java.time.Duration.between(fechaEntrada, fechaSalida).toDays();
        BigDecimal montoTotal = propiedad.getPrecioNoche().multiply(BigDecimal.valueOf(dias));
        
        // Crear reserva
        Reserva reserva = new Reserva(inquilino, propiedad, fechaEntrada, fechaSalida, politicaCancelacion);
        reserva = reservaDAO.save(reserva);
        
        // Si la propiedad permite reserva inmediata, crear pago
        if (propiedad.getPermiteReservaInmediata()) {
            Pago pago = pagoService.crearPago(reserva, montoTotal, Pago.MetodoPago.TARJETA_CREDITO);
            reserva.setPago(pago);
            
            // Confirmar reserva automáticamente
            confirmarReserva(reserva.getId());
            
            // Notificar al propietario
            crearNotificacion(propiedad.getPropietario(), "Nueva reserva confirmada", 
                             "Se ha realizado una reserva inmediata en tu propiedad: " + propiedad.getTitulo(), "RESERVA");
        } else {
            // Crear solicitud de reserva
            SolicitudReserva solicitud = new SolicitudReserva(reserva);
            solicitudReservaDAO.save(solicitud);
            reserva.setSolicitudReserva(solicitud);
            
            // Notificar al propietario
            crearNotificacion(propiedad.getPropietario(), "Nueva solicitud de reserva", 
                             "Tienes una nueva solicitud de reserva para tu propiedad: " + propiedad.getTitulo(), "SOLICITUD");
        }
        
        return reserva;
    }
    
    @Transactional
    public void confirmarReserva(Long reservaId) {
        Reserva reserva = obtenerReservaPorId(reservaId);
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva no encontrada");
        }
        
        reserva.confirmarReserva();
        reservaDAO.save(reserva);
    }
    
    @Transactional
    public boolean confirmarSolicitudReserva(Long solicitudId) {
        SolicitudReserva solicitud = solicitudReservaDAO.findById(solicitudId).orElse(null);
        if (solicitud == null || solicitud.getConfirmada()) return false;
        
        // Confirmar solicitud
        solicitud.confirmarSolicitud();
        solicitudReservaDAO.save(solicitud);
        
        // Crear pago
        Reserva reserva = solicitud.getReserva();
        Propiedad propiedad = reserva.getPropiedad();
        long dias = java.time.Duration.between(reserva.getFechaEntrada(), reserva.getFechaSalida()).toDays();
        BigDecimal montoTotal = propiedad.getPrecioNoche().multiply(BigDecimal.valueOf(dias));
        
        Pago pago = pagoService.crearPago(reserva, montoTotal, Pago.MetodoPago.TARJETA_CREDITO);
        pagoService.completarPago(pago.getId());
        
        // Notificar al inquilino
        crearNotificacion(reserva.getInquilino(), "Reserva confirmada", 
                         "Tu solicitud de reserva ha sido confirmada para la propiedad: " + propiedad.getTitulo(), "RESERVA");
        
        return true;
    }
    
    @Transactional
    public boolean rechazarSolicitudReserva(Long solicitudId) {
        SolicitudReserva solicitud = solicitudReservaDAO.findById(solicitudId).orElse(null);
        if (solicitud == null || solicitud.getConfirmada()) return false;
        
        // Rechazar solicitud
        solicitud.rechazarSolicitud();
        solicitudReservaDAO.save(solicitud);
        
        // Reembolsar si ya se había pagado
        if (solicitud.getReserva().getPagado()) {
            Pago pago = solicitud.getReserva().getPago();
            pagoService.reembolsarPago(pago.getId());
        }
        
        // Notificar al inquilino
        crearNotificacion(solicitud.getReserva().getInquilino(), "Reserva rechazada", 
                         "Tu solicitud de reserva ha sido rechazada para la propiedad: " + 
                         solicitud.getReserva().getPropiedad().getTitulo() + 
                         ". Se ha reembolsado tu pago.", "RESERVA");
        
        return true;
    }
    
    public Reserva obtenerReservaPorId(Long id) {
        return reservaDAO.findById(id).orElse(null);
    }
    
    public List<Reserva> obtenerReservasDeInquilino(Long inquilinoId) {
        return reservaDAO.findByInquilinoId(inquilinoId);
    }
    
    public List<Reserva> obtenerReservasDePropiedad(Long propiedadId) {
        return reservaDAO.findByPropiedadId(propiedadId);
    }
    
    public List<Reserva> obtenerReservasActivas() {
        return reservaDAO.findActivas(LocalDateTime.now());
    }
    
    @Transactional
    public void actualizarReserva(Reserva reserva) {
        reservaDAO.save(reserva);
    }
    
    public Reserva obtenerReservaPorSesion(String sessionId) {
        return reservaDAO.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada para sesión: " + sessionId));
    }
    
    private void crearNotificacion(Usuario usuario, String titulo, String mensaje, String tipo) {
        notificacionService.crearNotificacion(usuario, titulo, mensaje, tipo);
    }
}