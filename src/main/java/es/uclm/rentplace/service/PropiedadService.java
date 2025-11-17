package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Propiedad;
//import es.uclm.rentplace.entity.Propietario;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PropiedadService {
	
	@Autowired
	private PropiedadDAO propiedadPersistence;
	
	public boolean registrarPropiedad(String titulo, String ciudad, String tipoInmueble, BigDecimal precioNoche, Propietario propietario) {
		
		if (propietario == null) {
			return false;
		}
		if (titulo == null || titulo.isBlank()) {
			return false; 
		}
		if (precioNoche == null || precioNoche.signum() < 0) {
			return false;
		}
		
		Propiedad propiedad = new Propiedad();
		propiedad.setTitulo(titulo);
		propiedad.setCiudad(ciudad);
		propiedad.setPrecioNoche(precioNoche);
		propiedad.setPropietario(propietario);
		propiedad.setActivo(true);
		
		propiedadPersistence.save(propiedad);
		return true;
	}
		
		public Propiedad obtenerPorId(Long id) {
			return propiedadPersistence.findById(id).orElse(null);
		}
		
		public List<Propiedad> obtenerPropiedadesDePropietario(Propietario propietario) {
		    if (propietario == null) {
		        return List.of();
		    }
		    return propiedadPersistence.findByPropietarioId(propietario.getId());
		}
		
		public List<Propiedad> listarActivas() {
			return propiedadPersistence.findByActivoTrue();
		}
		
	}


