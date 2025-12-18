// src/main/java/es/uclm/rentplace/persistence/ReservaDAO.java
package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaDAO extends JpaRepository<Reserva, Long> {
    
    // Método para encontrar reservas solapadas
    @Query("SELECT r FROM Reserva r WHERE r.propiedad.id = :propiedadId AND " +
           "((r.fechaEntrada <= :fechaSalida AND r.fechaSalida >= :fechaSalida) OR " +
           "(r.fechaEntrada <= :fechaEntrada AND r.fechaSalida >= :fechaEntrada) OR " +
           "(:fechaEntrada <= r.fechaEntrada AND :fechaSalida >= r.fechaSalida)) AND " +
           "r.reservaConfirmada = true")
    List<Reserva> findSolapadas(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida);
    
    // Método para encontrar reservas por inquilino
    List<Reserva> findByInquilinoId(Long inquilinoId);
}