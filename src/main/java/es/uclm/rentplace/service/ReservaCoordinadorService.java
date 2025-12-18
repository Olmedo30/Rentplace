// src/main/java/es/uclm/rentplace/service/ReservaCoordinadorService.java
package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Pago;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ReservaCoordinadorService {
    
    private final PropiedadService propiedadService;
    private final DisponibilidadService disponibilidadService;
    private final ReservaService reservaService;
    private final PagoService pagoService;
    
    @Autowired
    public ReservaCoordinadorService(PropiedadService propiedadService, 
                                    DisponibilidadService disponibilidadService,
                                    ReservaService reservaService,
                                    PagoService pagoService) {
        this.propiedadService = propiedadService;
        this.disponibilidadService = disponibilidadService;
        this.reservaService = reservaService;
        this.pagoService = pagoService;
    }
    
    @Transactional
    public Reserva realizarReservaCompleta(Usuario inquilino, Long propiedadId, 
                                        LocalDateTime fechaEntrada, LocalDateTime fechaSalida,
                                        Reserva.PoliticaCancelacion politicaCancelacion,
                                        BigDecimal montoTotal, Pago.MetodoPago metodoPago) {
        
        // 1. Verificar disponibilidad
        if (!disponibilidadService.verificarDisponibilidad(propiedadId, fechaEntrada, fechaSalida)) {
            return null;
        }
        
        // 2. Crear reserva
        Reserva reserva = reservaService.crearReserva(inquilino, propiedadId, fechaEntrada, fechaSalida, politicaCancelacion);
        if (reserva == null) {
            return null;
        }
        
        // 3. Procesar pago
        pagoService.crearPago(reserva, montoTotal, metodoPago);
        
        return reserva;
    }
}