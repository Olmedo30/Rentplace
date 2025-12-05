package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.persistence.ReservaDAO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DisponibilidadService {

    private final ReservaDAO reservaDAO; // Usar el DAO directamente
    private final PropiedadService propiedadService;

    @Autowired
    public DisponibilidadService(ReservaDAO reservaDAO, PropiedadService propiedadService) {
        this.reservaDAO = reservaDAO;
        this.propiedadService = propiedadService;
    }

    @Transactional
    public boolean verificarDisponibilidad(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
        Propiedad propiedad = propiedadService.obtenerPorId(propiedadId);
        if (propiedad == null || !propiedad.getActivo()) {
            return false;
        }

        List<Reserva> reservasSolapadas = reservaDAO.findSolapadas(
            propiedadId, fechaEntrada, fechaSalida
        );
        
        return reservasSolapadas.isEmpty();
    }
    
    @Transactional
    public List<Propiedad> buscarPropiedadesDisponibles(String ciudad, String tipoInmueble, 
                                                       BigDecimal precioMax, 
                                                       LocalDateTime fechaEntrada, 
                                                       LocalDateTime fechaSalida) {
        List<Propiedad> propiedades = propiedadService.buscarAvanzado(ciudad, tipoInmueble, precioMax, true);
        return propiedades.stream()
                .filter(p -> verificarDisponibilidad(p.getId(), fechaEntrada, fechaSalida))
                .toList();
    }
}