package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Disponibilidad;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibilidadDAO extends JpaRepository<Disponibilidad, Long> {

    // Todas las disponibilidades de una propiedad
    List<Disponibilidad> findByPropiedadId(Long propiedadId);

    // Disponibilidades que cubren un intervalo (fechaInicio <= start AND fechaFin >= end)
    List<Disponibilidad> findByPropiedadIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long propiedadId, LocalDate fechaInicio, LocalDate fechaFin);

    // Disponibilidades que se solapan con un intervalo dado
    List<Disponibilidad> Disponibilidades_Sopalan(
            Long propiedadId, LocalDate fechaFin, LocalDate fechaInicio);
}

