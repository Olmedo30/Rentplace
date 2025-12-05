package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoDAO extends JpaRepository<Pago, Long> {
    
    // Corrección: Definir el método para buscar pagos por inquilino
    @Query("SELECT p FROM Pago p WHERE p.reserva.inquilino.id = :inquilinoId")
    List<Pago> findByReservaInquilinoId(Long inquilinoId);
    
    // Corrección: Definir el método para buscar pagos por propietario
    @Query("SELECT p FROM Pago p WHERE p.reserva.propiedad.propietario.id = :propietarioId")
    List<Pago> findByReservaPropiedadPropietarioId(Long propietarioId);
}