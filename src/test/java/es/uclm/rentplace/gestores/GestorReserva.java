package es.uclm.rentplace.gestores;

import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.ReservaDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GestorReserva {

    @Autowired
	public ReservaDAO reservaDAO;

    
     // Busca reservas que se solapen con el rango de fechas dado
     
    public List<Reserva> buscarReservasSolapadas(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
        if (propiedadId == null || propiedadId <= 0) {
            throw new IllegalArgumentException("ID de propiedad inválido.");
        }
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }
        if (fechaEntrada.isAfter(fechaSalida)) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser posterior a la fecha de salida.");
        }
        return reservaDAO.findSolapadas(propiedadId, fechaEntrada, fechaSalida);
    }

    
     // Busca todas las reservas de un inquilino
     
    public List<Reserva> buscarReservasPorInquilino(Long inquilinoId) {
        if (inquilinoId == null || inquilinoId <= 0) {
            throw new IllegalArgumentException("ID de inquilino inválido.");
        }
        return reservaDAO.findByInquilinoId(inquilinoId);
    }

    
     // Verifica si hay disponibilidad para reserva inmediata (solo reservas confirmadas)
     
    public boolean verificarDisponibilidadInmediata(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
        if (propiedadId == null || propiedadId <= 0) {
            throw new IllegalArgumentException("ID de propiedad inválido.");
        }
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }
        if (fechaEntrada.isAfter(fechaSalida)) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser posterior a la fecha de salida.");
        }
        
        List<Reserva> reservasConfirmadas = reservaDAO.findReservasConfirmadasEnRango(propiedadId, fechaEntrada, fechaSalida);
        return reservasConfirmadas.isEmpty();
    }

    
     // Verifica si hay disponibilidad para reserva no inmediata (todas las reservas)
     
    public boolean verificarDisponibilidadNoInmediata(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
        if (propiedadId == null || propiedadId <= 0) {
            throw new IllegalArgumentException("ID de propiedad inválido.");
        }
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }
        if (fechaEntrada.isAfter(fechaSalida)) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser posterior a la fecha de salida.");
        }
        
        List<Reserva> todasReservas = reservaDAO.findTodasReservasEnRango(propiedadId, fechaEntrada, fechaSalida);
        return todasReservas.isEmpty();
    }

    
     // Calcula el importe total de una reserva
     
    public BigDecimal calcularImporteTotal(LocalDateTime fechaEntrada, LocalDateTime fechaSalida, BigDecimal precioNoche) {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }
        if (precioNoche == null || precioNoche.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio por noche debe ser mayor a cero.");
        }
        if (fechaEntrada.isAfter(fechaSalida)) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser posterior a la fecha de salida.");
        }
        
        long dias = java.time.Duration.between(fechaEntrada, fechaSalida).toDays();
        if (dias <= 0) {
            dias = 1; // Mínimo 1 día
        }
        return precioNoche.multiply(BigDecimal.valueOf(dias));
    }

    
     // Valida que las fechas de una reserva sean correctas
     
    public boolean validarFechasReserva(LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null) {
            return false;
        }
        if (fechaEntrada.isBefore(LocalDateTime.now())) {
            return false;
        }
        if (fechaEntrada.isAfter(fechaSalida) || fechaEntrada.isEqual(fechaSalida)) {
            return false;
        }
        return true;
    }

    
     // Verifica si una reserva está activa (no ha finalizado)
     
    public boolean estaReservaActiva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula.");
        }
        if (reserva.getFechaSalida() == null) {
            throw new IllegalArgumentException("La reserva no tiene fecha de salida.");
        }
        return LocalDateTime.now().isBefore(reserva.getFechaSalida());
    }

    
     // Obtiene una reserva por su ID
     
    public Reserva obtenerReservaPorId(Long reservaId) {
        if (reservaId == null || reservaId <= 0) {
            throw new IllegalArgumentException("ID de reserva inválido.");
        }
        return reservaDAO.findById(reservaId).orElse(null);
    }

    
     // Confirma una reserva
     
    public void confirmarReserva(Long reservaId) {
        if (reservaId == null || reservaId <= 0) {
            throw new IllegalArgumentException("ID de reserva inválido.");
        }
        Reserva reserva = reservaDAO.findById(reservaId).orElse(null);
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no existe.");
        }
        reserva.setReservaConfirmada(true);
        reservaDAO.save(reserva);
    }
}


