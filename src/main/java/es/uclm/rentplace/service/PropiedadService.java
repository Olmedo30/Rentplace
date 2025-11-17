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
	
	// Registramos una nueva propiedad
	// Devolvemos un true si se ha guardado correctamente, false si los datos son inválidos
	public boolean registrarPropiedad(String titulo, String ciudad, String tipoInmueble, BigDecimal precioNoche, Propietario propietario) {
		
		// Validaciones básicas
		if (propietario == null) {
			return false;// Que el propietario sea válido
		}
		if (titulo == null || titulo.isBlank()) {
			return false; // Título campo obligatorio
		}
		if (precioNoche == null || precioNoche.signum() < 0) {
			return false;// Que el precio sea válido y no negativo
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
		// Obtenemos una propiedad por su id
		public Propiedad obtenerPorId(Long id) {
			return propiedadPersistence.findById(id).orElse(null);
		}
		
		//Obtener todas las propiedades de un propietario
		public List<Propiedad> obtenerPropiedadesDePropietario(Propietario propietario) {
		    if (propietario == null) {
		        return List.of();
		    }
		    return propiedadPersistence.findByPropietarioId(propietario.getId());
		}
		
		//Listar propiedades que estén activas
		public List<Propiedad> listarActivas() {
			return propiedadPersistence.findByActivoTrue();
		}
		
		// Buscamos propiedades por ciudad
		public List<Propiedad> buscarPorCiudad(String ciudad) {
		    return propiedadPersistence.findByCiudad(ciudad);
		}
		
		// Buscamos propiedades por tipo de inmueble
		 public List<Propiedad> buscarPorTipo(String tipo) {
		    return propiedadPersistence.findByTipoInmueble(tipo);
		 }
		 // Buscamos por precio máximo por noche
		 public List<Propiedad> buscarPorPrecioMaximo(BigDecimal precioMax) {
		    return propiedadPersistence.findByPrecioNocheLessThanEqual(precioMax);
		 }
		 // Desactivar una propiedad
		 public boolean desactivarPropiedad(Long id) {
			 Propiedad propiedad = propiedadPersistence.findById(id).orElse(null);
			 if(propiedad == null) {
				 return false;// Devuelve false si no existe la propiedad
			 }
			 propiedad.setActivo(false);
			 propiedadPersistence.save(propiedad);
			 return true;// Devuelve true si se desactivó correctamente
			 
		 }
		 // Activar una propiedad
		 public boolean activarPropiedad(Long id ) {
			 Propiedad propiedad = propiedadPersistence.findById(id).orElse(null);
			 if (propiedad == null) {
				 return false;// Devuelve false si no existe la propiedad
			 }
			 propiedad.setActivo(true);
			 propiedadPersistence.save(propiedad);
			 return true;// Devuelve true si se activó correctamente
		 }
	}


