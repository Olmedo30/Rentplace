package es.uclm.rentplace.gestores;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GestorPropiedad {

    @Autowired
	public PropiedadDAO propiedadDAO;

    // Buscar propiedades activas
    public List<Propiedad> buscarPropiedadesActivas() {
        return propiedadDAO.findByActivoTrue();
    }

    // Buscar tipos únicos de inmuebles activos
    public List<String> obtenerTiposInmueblesActivos() {
        return propiedadDAO.findDistinctTipoInmuebleByActivoTrue();
    }

    // Búsqueda avanzada de propiedades
    public List<Propiedad> buscarPropiedadesAvanzadas(String ciudad, String tipoInmueble, BigDecimal precioMax, Boolean reservaInmediata) {
        return propiedadDAO.buscarAvanzado(ciudad, tipoInmueble, precioMax, reservaInmediata);
    }

    // Buscar propiedades activas de un propietario
    public List<Propiedad> buscarPropiedadesActivasPorPropietario(Long propietarioId) {
        if (propietarioId == null || propietarioId <= 0) {
            throw new IllegalArgumentException("El ID del propietario no es válido.");
        }
        return propiedadDAO.findByPropietarioIdAndActivoTrue(propietarioId);
    }

    // Buscar propiedades en un rango de precios
    public List<Propiedad> buscarPorRangoDePrecios(BigDecimal precioMin, BigDecimal precioMax) {
        if (precioMin == null || precioMax == null || precioMin.compareTo(precioMax) > 0) {
            throw new IllegalArgumentException("El rango de precios es inválido.");
        }
        return propiedadDAO.findByPrecioNocheBetweenAndActivoTrue(precioMin, precioMax);
    }
}