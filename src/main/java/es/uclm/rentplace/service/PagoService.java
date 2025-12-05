package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.*;
import es.uclm.rentplace.persistence.PagoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PagoService {
    
    private final PagoDAO pagoDAO;
    private final ReservaService reservaService;
    private final NotificacionService notificacionService;
    
    @Autowired
    public PagoService(PagoDAO pagoDAO, ReservaService reservaService, NotificacionService notificacionService) {
        this.pagoDAO = pagoDAO;
        this.reservaService = reservaService;
        this.notificacionService = notificacionService;
    }
    
    @Transactional
    public Pago crearPago(Reserva reserva, BigDecimal monto, Pago.MetodoPago metodoPago) {
        if (reserva.getPago() != null) {
            return reserva.getPago();
        }
        
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setFechaPago(LocalDateTime.now());
        pago.setReferencia(UUID.randomUUID().toString());
        pago.setCompletado(false);
        
        return pagoDAO.save(pago);
    }
    
    @Transactional
    public void completarPago(Long pagoId) {
        Pago pago = pagoDAO.findById(pagoId).orElse(null);
        if (pago == null) {
            throw new IllegalArgumentException("Pago no encontrado");
        }
        
        if (pago.getCompletado()) {
            return;
        }
        
        pago.setCompletado(true);
        pagoDAO.save(pago);
        
        // Actualizar estado de la reserva
        Reserva reserva = pago.getReserva();
        reserva.setPagado(true);
        reservaService.actualizarReserva(reserva);
    }
    
    @Transactional
    public void reembolsarPago(Long pagoId) {
        Pago pago = pagoDAO.findById(pagoId).orElse(null);
        if (pago == null) {
            throw new IllegalArgumentException("Pago no encontrado");
        }
        
        if (!pago.getCompletado()) {
            return;
        }
        
        pago.setCompletado(false);
        pagoDAO.save(pago);
        
        // Actualizar estado de la reserva
        Reserva reserva = pago.getReserva();
        reserva.setPagado(false);
        reservaService.actualizarReserva(reserva);
    }
    
    @Transactional
    public Long confirmPaymentAndReserva(String sessionId) {
        Reserva reserva = reservaService.obtenerReservaPorSesion(sessionId);
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva no encontrada para la sesión: " + sessionId);
        }
        
        // Completar el pago
        Pago pago = crearPago(reserva, calcularTotalReserva(reserva), Pago.MetodoPago.TARJETA_CREDITO);
        completarPago(pago.getId());
        
        // Confirmar la reserva si es inmediata
        if (reserva.getPropiedad().getPermiteReservaInmediata()) {
            reservaService.confirmarReserva(reserva.getId());
        }
        
        return reserva.getId();
    }
    
    public Pago obtenerPagoPorId(Long pagoId) {
        return pagoDAO.findById(pagoId).orElse(null);
    }
    
    public List<Pago> obtenerPagosDeInquilino(Long inquilinoId) {
        return pagoDAO.findByReservaInquilinoId(inquilinoId);
    }
    
    public List<Pago> obtenerPagosDePropietario(Long propietarioId) {
        return pagoDAO.findByReservaPropiedadPropietarioId(propietarioId);
    }
    
    public BigDecimal calcularTotalReserva(Reserva reserva) {
        long dias = java.time.Duration.between(reserva.getFechaEntrada(), reserva.getFechaSalida()).toDays();
        return reserva.getPropiedad().getPrecioNoche().multiply(BigDecimal.valueOf(dias));
    }
}