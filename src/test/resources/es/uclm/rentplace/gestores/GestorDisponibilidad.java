package es.uclm.rentplace.gestores;

import es.uclm.rentplace.entity.Disponibilidad;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.persistence.DisponibilidadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class GestorDisponibilidad {

    @Autowired
    private DisponibilidadDAO disponibilidadDAO;

    public List<Disponibilidad> buscarDisponibilidadesPorPropiedad(Long propiedadId) {
        if (propiedadId == null || propiedadId <= 0) {
            throw new IllegalArgumentException("ID de propiedad inválido.");
        }
        return disponibilidadDAO.findByPropiedadId(propiedadId);
    }

    public List<Disponibilidad> buscarDisponibilidadesEnIntervalo(Long propiedadId, LocalDate fechaInicio, LocalDate fechaFin) {
        if (propiedadId == null || propiedadId <= 0 || fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Parámetros inválidos para buscar disponibilidades en el intervalo.");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        return disponibilidadDAO.findByPropiedadIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                propiedadId, fechaInicio, fechaFin);
    }

    public Disponibilidad crearDisponibilidad(Propiedad propiedad, LocalDate fechaInicio, LocalDate fechaFin, Boolean disponible, BigDecimal precioEspecial) {
        if (propiedad == null || fechaInicio == null || fechaFin == null || disponible == null || fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("Parámetros inválidos para crear disponibilidad.");
        }
        Disponibilidad disponibilidad = new Disponibilidad(propiedad, fechaInicio, fechaFin, disponible, precioEspecial);
        return disponibilidadDAO.save(disponibilidad);
    }
}