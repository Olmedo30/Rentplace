package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PropiedadService {
    
    private final PropiedadDAO propiedadDAO;
    
    @Autowired
    public PropiedadService(PropiedadDAO propiedadDAO) {
        this.propiedadDAO = propiedadDAO;
    }

    public Propiedad registrarPropiedad(Usuario propietario, String titulo, String descripcion, 
                                        String direccion, String ciudad, String tipoInmueble,
                                        Integer habitaciones, Integer capacidad, BigDecimal precioNoche,
                                        String politicaCancelacion, Boolean permiteReservaInmediata) {
        Propiedad propiedad = new Propiedad();
        propiedad.setPropietario(propietario);
        propiedad.setTitulo(titulo);
        propiedad.setDescripcion(descripcion);
        propiedad.setDireccion(direccion);
        propiedad.setCiudad(ciudad);
        propiedad.setTipoInmueble(tipoInmueble);
        propiedad.setHabitaciones(habitaciones);
        propiedad.setCapacidad(capacidad);
        propiedad.setPrecioNoche(precioNoche);
        propiedad.setPoliticaCancelacion(politicaCancelacion);
        propiedad.setPermiteReservaInmediata(permiteReservaInmediata);
        propiedad.setActivo(true);
        return propiedadDAO.save(propiedad);
    }
    
    public Propiedad obtenerPorId(Long id) {
        return propiedadDAO.findById(id).orElse(null);
    }
    
    public List<Propiedad> listarPropiedadesActivas() {
        return propiedadDAO.findByActivoTrue();
    }
    
    public List<Propiedad> obtenerPropiedadesDePropietario(Long propietarioId) {
        return propiedadDAO.findByPropietarioId(propietarioId);
    }
    
    public List<Propiedad> obtenerPropiedadesActivasDePropietario(Long propietarioId) {
        return propiedadDAO.findByPropietarioIdAndActivoTrue(propietarioId);
    }
    
    public List<Propiedad> buscarPorCiudad(String ciudad) {
        return propiedadDAO.findByCiudadContainingIgnoreCaseAndActivoTrue(ciudad);
    }
    
    public List<Propiedad> buscarPorTipo(String tipo) {
        return propiedadDAO.findByTipoInmuebleAndActivoTrue(tipo);
    }
    
    public List<Propiedad> buscarPorPrecioMaximo(BigDecimal precioMax) {
        return propiedadDAO.findByPrecioNocheLessThanEqualAndActivoTrue(precioMax);
    }
    
    public List<Propiedad> buscarPorReservaInmediata(Boolean reservaInmediata) {
        return propiedadDAO.findByPermiteReservaInmediataAndActivoTrue(reservaInmediata);
    }
    
    public List<Propiedad> buscarAvanzado(String ciudad, String tipoInmueble, 
                                         BigDecimal precioMax, Boolean reservaInmediata) {
        return propiedadDAO.buscarAvanzado(ciudad, tipoInmueble, precioMax, reservaInmediata);
    }
    
    public List<String> obtenerTiposInmuebleUnicos() {
        return propiedadDAO.findDistinctTipoInmuebleByActivoTrue();
    }
    
    public List<String> obtenerCiudadesUnicas() {
        return propiedadDAO.findDistinctCiudadByActivoTrue();
    }
    
    public List<Propiedad> buscarPorRangoPrecios(BigDecimal precioMin, BigDecimal precioMax) {
        return propiedadDAO.findByPrecioNocheBetweenAndActivoTrue(precioMin, precioMax);
    }
    
    public boolean desactivarPropiedad(Long id) {
        Propiedad propiedad = propiedadDAO.findById(id).orElse(null);
        if (propiedad == null) return false;
        propiedad.setActivo(false);
        propiedadDAO.save(propiedad);
        return true;
    }
    
    public boolean activarPropiedad(Long id) {
        Propiedad propiedad = propiedadDAO.findById(id).orElse(null);
        if (propiedad == null) return false;
        propiedad.setActivo(true);
        propiedadDAO.save(propiedad);
        return true;
    }
    
    public boolean eliminarPropiedad(Long id) {
        return desactivarPropiedad(id);
    }
    
    public Propiedad actualizarPropiedad(Propiedad propiedadActualizada) {
        Propiedad propiedadExistente = propiedadDAO.findById(propiedadActualizada.getId()).orElse(null);
        if (propiedadExistente == null) {
            return null;
        }
        
        propiedadExistente.setTitulo(propiedadActualizada.getTitulo());
        propiedadExistente.setDescripcion(propiedadActualizada.getDescripcion());
        propiedadExistente.setDireccion(propiedadActualizada.getDireccion());
        propiedadExistente.setCiudad(propiedadActualizada.getCiudad());
        propiedadExistente.setTipoInmueble(propiedadActualizada.getTipoInmueble());
        propiedadExistente.setHabitaciones(propiedadActualizada.getHabitaciones());
        propiedadExistente.setCapacidad(propiedadActualizada.getCapacidad());
        propiedadExistente.setPrecioNoche(propiedadActualizada.getPrecioNoche());
        propiedadExistente.setPoliticaCancelacion(propiedadActualizada.getPoliticaCancelacion());
        propiedadExistente.setPermiteReservaInmediata(propiedadActualizada.getPermiteReservaInmediata());
        
        return propiedadDAO.save(propiedadExistente);
    }
    
    public List<Propiedad> obtenerPropiedadesRecomendadas() {
        return propiedadDAO.findByActivoTrue().stream()
                .limit(6)
                .toList();
    }
}