package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Disponibilidad;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.persistence.DisponibilidadDAO;
import es.uclm.rentplace.persistence.PropiedadDAO;
//import es.uclm.rentplace.persistence.PropietarioDAO; // si lo necesitas
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DisponibilidadService {

    @Autowired
    private DisponibilidadDAO disponibilidadDAO;

    @Autowired
    private PropiedadDAO propiedadDAO;

    /**
     * Añadir una disponibilidad a una propiedad
     */
    public Optional<Disponibilidad> addDisponibilidad(Long propiedadId, LocalDate inicio, LocalDate fin, Boolean disponible, BigDecimal precioEspecial) {
        var propOpt = propiedadDAO.findById(propiedadId);
        if (propOpt.isEmpty()) return Optional.empty();

        Propiedad p = propOpt.get();
        Disponibilidad d = new Disponibilidad(p, inicio, fin, disponible, precioEspecial);
        return Optional.of(disponibilidadDAO.save(d));
    }

    /**
     * Listar disponibilidades de una propiedad
     */
    public List<Disponibilidad> listarDisponibilidades(Long propiedadId) {
        return disponibilidadDAO.findByPropiedadId(propiedadId);
    }

    /**
     * Buscar propiedades disponibles en un rango
     */
    public List<Propiedad> buscarPropiedadesDisponibles(LocalDate start, LocalDate end) {
        return propiedadDAO.findAvailablePropertiesBetween(start, end);
    }

    /**
     * Comprobar si una propiedad concreta está disponible en un rango
     * (usa la consulta de propiedades disponibles filtrando por id)
     */
    public boolean isPropiedadDisponible(Long propiedadId, LocalDate start, LocalDate end) {
        List<Propiedad> disponibles = propiedadDAO.findAvailablePropertiesBetween(start, end);
        return disponibles.stream().anyMatch(p -> p.getId().equals(propiedadId));
    }
}
