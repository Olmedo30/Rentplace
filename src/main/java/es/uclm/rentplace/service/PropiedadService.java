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
	
	public boolean registrarPropiedad(String titulo, String ciudad, String tipoInmueble, BigDecimal precioNoche, Usuario propietario) {
		
		if (propietario == null) {
			return false;
		}
		if (titulo == null || titulo.isBlank()) {
			return false; 
		}
		if (precioNoche == null || precioNoche.signum() < 0) {
			return false;
		}
	}

}
