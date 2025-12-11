package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Disponibilidad;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibilidadDAO extends JpaRepository<Disponibilidad, Long> {

    // Todas las disponibilidades de una propiedad
    List<Disponibilidad> findByPropiedadId(Long propiedadId);

    // Disponibilidades que cubren un intervalo
    List<Disponibilidad> findByPropiedadIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long propiedadId, LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT d FROM Disponibilidad d WHERE d.propiedad.id = :propiedadId " +
           "AND d.fechaInicio <= :fechaFin AND d.fechaFin >= :fechaInicio")
    List<Disponibilidad> Disponibilidades_Sopalan(
            @Param("propiedadId") Long propiedadId, 
            @Param("fechaFin") LocalDate fechaFin, 
            @Param("fechaInicio") LocalDate fechaInicio);
}