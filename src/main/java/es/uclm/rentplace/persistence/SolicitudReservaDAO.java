package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.SolicitudReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudReservaDAO extends JpaRepository<SolicitudReserva, Long> {
    
    // Buscar solicitudes no confirmadas
    List<SolicitudReserva> findByConfirmadaFalse();
    
    // Buscar solicitudes por reserva
    SolicitudReserva findByReservaId(Long reservaId);
}