// src/main/java/es/uclm/rentplace/service/PropiedadService.java
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
    
    @Autowired
    private PropiedadDAO propiedadDAO;
    
    public Propiedad obtenerPorId(Long id) {
        return propiedadDAO.findById(id).orElse(null);
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
    
    public List<Propiedad> buscarPropiedadesAvanzado(String ciudad, String tipoInmueble, 
                                                   BigDecimal precioMax, Boolean reservaInmediata) {
        return propiedadDAO.buscarAvanzado(ciudad, tipoInmueble, precioMax, reservaInmediata);
    }
    
    // Otros métodos existentes...
    public List<Propiedad> listarPropiedadesActivas() {
        return propiedadDAO.findByActivoTrue();
    }
    
    public List<Propiedad> obtenerPropiedadesDePropietario(Long propietarioId) {
        return propiedadDAO.findByPropietarioId(propietarioId);
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
    
    public List<String> obtenerTiposInmuebleUnicos() {
        return propiedadDAO.findDistinctTipoInmuebleByActivoTrue();
    }
    
    public List<String> obtenerCiudadesUnicas() {
        return propiedadDAO.findDistinctCiudadByActivoTrue();
    }
    public Propiedad obtenerPropiedadPorId(Long id) {
        return propiedadDAO.findById(id).orElse(null);
    }
}