package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Propiedad;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropiedadDAO extends JpaRepository<Propiedad, Long> {
	
	List<Propiedad> findByCiudad(String ciudad); // Buscar por ciudad
	List<Propiedad> findByTipoInmueble(String tipoInmueble); // Buscar por tipo de inmueble
	List<Propiedad> findByPropietarioId(Long propietario); // Buscar por id del propietario
	List<Propiedad> findByPropietarioUsername(String username);// Buscar por username del propietario
	List<Propiedad> findByActivoTrue();// Propiedades activas
	List<Propiedad> findByPrecioNocheLessThanEqual(BigDecimal precioMaximo);// Filtramos por precio máximo por noche

}
