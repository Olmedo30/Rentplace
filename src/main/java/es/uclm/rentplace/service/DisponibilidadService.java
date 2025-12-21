// src/main/java/es/uclm/rentplace/service/DisponibilidadService.java
package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.persistence.ReservaDAO;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DisponibilidadService {

    @Autowired
    private ReservaDAO reservaDAO;
    
    @Autowired
    private PropiedadDAO propiedadDAO;

    public boolean verificarDisponibilidad(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
        Propiedad propiedad = propiedadDAO.findById(propiedadId).orElse(null);
        if (propiedad == null || !propiedad.getActivo()) {
            return false;
        }
        
        List<Reserva> reservasSolapadas = reservaDAO.findSolapadas(propiedadId, fechaEntrada, fechaSalida);
        
        return reservasSolapadas.isEmpty();
    }
    
    public List<Propiedad> buscarPropiedadesDisponibles(String ciudad, String tipoInmueble, 
                                                       BigDecimal precioMax, 
                                                       LocalDateTime fechaEntrada, 
                                                       LocalDateTime fechaSalida) {
        List<Propiedad> propiedades = propiedadDAO.buscarAvanzado(ciudad, tipoInmueble, precioMax, true);
        return propiedades.stream()
                .filter(p -> verificarDisponibilidad(p.getId(), fechaEntrada, fechaSalida))
                .toList();
    }
}