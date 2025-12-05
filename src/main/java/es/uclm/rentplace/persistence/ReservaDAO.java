package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaDAO extends JpaRepository<Reserva, Long> {
    
    // Buscar reservas por inquilino
    List<Reserva> findByInquilinoId(Long inquilinoId);
    
    // Buscar reservas por propiedad
    List<Reserva> findByPropiedadId(Long propiedadId);
    
    // Buscar reservas activas (fechas futuras)
    @Query("SELECT r FROM Reserva r WHERE r.fechaSalida > :ahora")
    List<Reserva> findActivas(LocalDateTime ahora);
    
    // Para encontrar una reserva por sesión (simulación)
    @Query("SELECT r FROM Reserva r WHERE r.solicitudReserva.sessionId = :sessionId")
    Optional<Reserva> findBySessionId(@Param("sessionId") String sessionId);
    
    // Buscar reservas por fechas y propiedad (para verificar disponibilidad)
    @Query("SELECT r FROM Reserva r WHERE r.propiedad.id = :propiedadId AND " +
           "((r.fechaEntrada <= :fechaSalida AND r.fechaSalida >= :fechaSalida) OR " +
           "(r.fechaEntrada <= :fechaEntrada AND r.fechaSalida >= :fechaEntrada) OR " +
           "(:fechaEntrada <= r.fechaEntrada AND :fechaSalida >= r.fechaSalida)) AND " +
           "r.reservaConfirmada = true")
    List<Reserva> findSolapadas(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida);
}