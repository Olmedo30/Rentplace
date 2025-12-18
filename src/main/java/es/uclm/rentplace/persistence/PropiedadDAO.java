package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropiedadDAO extends JpaRepository<Propiedad, Long>, JpaSpecificationExecutor<Propiedad> {
 
    // Propiedades activas
    List<Propiedad> findByActivoTrue();
    
    // Obtener valores únicos para filtros
    @Query("SELECT DISTINCT p.tipoInmueble FROM Propiedad p WHERE p.activo = true")
    List<String> findDistinctTipoInmuebleByActivoTrue();
    
    @Query("SELECT DISTINCT p.ciudad FROM Propiedad p WHERE p.activo = true")
    List<String> findDistinctCiudadByActivoTrue();
    
    // Búsquedas básicas
    List<Propiedad> findByCiudadContainingIgnoreCaseAndActivoTrue(String ciudad);
    
    List<Propiedad> findByTipoInmuebleAndActivoTrue(String tipoInmueble);
    
    List<Propiedad> findByPrecioNocheLessThanEqualAndActivoTrue(BigDecimal precioMax);
    
    List<Propiedad> findByPermiteReservaInmediataAndActivoTrue(Boolean reservaInmediata);
    
    // Propiedades de un propietario (solo activas para búsquedas)
    List<Propiedad> findByPropietarioIdAndActivoTrue(Long propietarioId);
    
    // Todas las propiedades de un propietario (para gestión)
    List<Propiedad> findByPropietarioId(Long propietarioId);
    
    // Búsqueda avanzada con filtros múltiples
    @Query("SELECT p FROM Propiedad p WHERE " +
           "(:ciudad IS NULL OR LOWER(p.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%'))) AND " +
           "(:tipoInmueble IS NULL OR p.tipoInmueble = :tipoInmueble) AND " +
           "(:precioMax IS NULL OR p.precioNoche <= :precioMax) AND " +
           "(:reservaInmediata IS NULL OR p.permiteReservaInmediata = :reservaInmediata) AND " +
           "p.activo = true")
    List<Propiedad> buscarAvanzado(String ciudad, String tipoInmueble, 
                                  BigDecimal precioMax, Boolean reservaInmediata);
    
    // Búsqueda por rango de precios (mejora adicional)
    List<Propiedad> findByPrecioNocheBetweenAndActivoTrue(BigDecimal precioMin, BigDecimal precioMax);
}