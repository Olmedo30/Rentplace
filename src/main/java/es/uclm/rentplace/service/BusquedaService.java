package es.uclm.rentplace.service;

import es.uclm.rentplace.dto.BusquedaPropiedadDTO;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BusquedaService {

    @Autowired
    private PropiedadDAO propiedadDAO;

    public Page<Propiedad> buscarPropiedades(BusquedaPropiedadDTO criterios, Pageable pageable) {
        List<Propiedad> todasPropiedades = propiedadDAO.findByActivoTrue();
        List<Propiedad> propiedadesFiltradas = filtrarPropiedades(todasPropiedades, criterios);
        
        // Paginación manual
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), propiedadesFiltradas.size());
        
        if (start > end) {
            return new PageImpl<>(List.of(), pageable, propiedadesFiltradas.size());
        }
        
        List<Propiedad> pageContent = propiedadesFiltradas.subList(start, end);
        return new PageImpl<>(pageContent, pageable, propiedadesFiltradas.size());
    }

    private List<Propiedad> filtrarPropiedades(List<Propiedad> propiedades, BusquedaPropiedadDTO criterios) {
        Stream<Propiedad> stream = propiedades.stream();
        
        if (criterios.getTipoInmueble() != null && !criterios.getTipoInmueble().isEmpty()) {
            stream = stream.filter(p -> criterios.getTipoInmueble().equals(p.getTipoInmueble()));
        }
        
        if (criterios.getCiudad() != null && !criterios.getCiudad().isEmpty()) {
            stream = stream.filter(p -> p.getCiudad().toLowerCase().contains(criterios.getCiudad().toLowerCase()));
        }
        
        if (criterios.getCapacidadMinima() != null) {
            stream = stream.filter(p -> p.getCapacidad() >= criterios.getCapacidadMinima());
        }
        
        if (criterios.getPrecioMaximo() != null) {
            stream = stream.filter(p -> p.getPrecioNoche().compareTo(criterios.getPrecioMaximo()) <= 0);
        }
        
        return stream.collect(Collectors.toList());
    }

    public List<String> obtenerTiposInmuebleDisponibles() {
        return propiedadDAO.findDistinctTipoInmuebleByActivoTrue();
    }

    public List<String> obtenerCiudadesDisponibles() {
        return propiedadDAO.findDistinctCiudadByActivoTrue();
    }
    
    
}