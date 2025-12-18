// src/main/java/es/uclm/rentplace/service/PagoService.java
package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Pago;
import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.PagoDAO;
import es.uclm.rentplace.persistence.ReservaDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {
    
    @Autowired
    private PagoDAO pagoDAO;
    
    @Autowired
    private ReservaDAO reservaDAO;
    
    // Método para completar un pago
    @Transactional
    public void completarPago(Long pagoId) {
        Pago pago = pagoDAO.findById(pagoId).orElse(null);
        if (pago != null) {
            pago.setCompletado(true);
            pagoDAO.save(pago);
        }
    }
    
    // Método para obtener un pago por ID
    public Pago obtenerPagoPorId(Long pagoId) {
        return pagoDAO.findById(pagoId).orElse(null);
    }
    
    // Método para obtener pagos de un inquilino
    public List<Pago> obtenerPagosDeInquilino(Long inquilinoId) {
        return pagoDAO.findByReservaInquilinoId(inquilinoId);
    }
    
    // Método para obtener pagos de un propietario
    public List<Pago> obtenerPagosDePropietario(Long propietarioId) {
        return pagoDAO.findByReservaPropiedadPropietarioId(propietarioId);
    }
    
    // Método para reembolsar un pago
    @Transactional
    public void reembolsarPago(Long pagoId) {
        Pago pago = pagoDAO.findById(pagoId).orElse(null);
        if (pago != null) {
            pago.setCompletado(false);
            pagoDAO.save(pago);
        }
    }
    
    // Método para crear un pago
    @Transactional
    public Pago crearPago(Reserva reserva, BigDecimal monto, Pago.MetodoPago metodoPago) {
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setFechaPago(LocalDateTime.now());
        pago.setReferencia(java.util.UUID.randomUUID().toString());
        pago.setCompletado(false);
        
        return pagoDAO.save(pago);
    }
    
    // Método para calcular el total de una reserva
    public BigDecimal calcularTotalReserva(Reserva reserva) {
        if (reserva == null) return BigDecimal.ZERO;
        
        long dias = java.time.temporal.ChronoUnit.DAYS.between(
            reserva.getFechaEntrada().toLocalDate(), 
            reserva.getFechaSalida().toLocalDate()
        );
        
        return reserva.getPropiedad().getPrecioNoche().multiply(BigDecimal.valueOf(dias));
    }
    
    // Método para confirmar pago y reserva (simulación)
    @Transactional
    public Long confirmPaymentAndReserva(String sessionId) {
        // En una implementación real, esto buscaría la reserva por sessionId
        // Para esta implementación, simplemente retorna un ID de ejemplo
        return 1L;
    }
}