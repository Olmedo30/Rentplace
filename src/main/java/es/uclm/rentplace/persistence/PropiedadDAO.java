package es.uclm.rentplace.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import es.uclm.rentplace.entity.Propiedad;
import java.util.List;
import java.time.LocalDate;
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
	
	  @Query(value = "" +
		        "SELECT p.* FROM propiedades p " +
		        "WHERE EXISTS ( " +
		        "   SELECT 1 FROM disponibilidades d " +
		        "   WHERE d.propiedad_id = p.propiedades_id " +
		        "     AND d.disponible = true " +
		        "     AND d.fecha_inicio <= :start " +
		        "     AND d.fecha_fin >= :end " +
		        ") " +
		        "AND NOT EXISTS ( " +
		        "   SELECT 1 FROM reservas r " +
		        "   WHERE r.propiedad_id = p.propiedades_id " +
		        "     AND r.estado IN ('pendiente','confirmada') " +   // estados que bloquean
		        "     AND r.fecha_inicio <= :end " +
		        "     AND r.fecha_fin >= :start " +
		        ")",
		        nativeQuery = true)
	List<Propiedad> findAvailablePropertiesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
