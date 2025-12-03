package es.uclm.rentplace.persistence;

import es.uclm.rentplace.entity.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PropiedadDAO extends JpaRepository<Propiedad, Long>, JpaSpecificationExecutor<Propiedad> {
 
 List<Propiedad> findByActivoTrue();
 
 @Query("SELECT DISTINCT p.tipoInmueble FROM Propiedad p WHERE p.activo = true")
 List<String> findDistinctTipoInmuebleByActivoTrue();
 
 @Query("SELECT DISTINCT p.ciudad FROM Propiedad p WHERE p.activo = true")
 List<String> findDistinctCiudadByActivoTrue();
 
 List<Propiedad> findByCiudadContainingIgnoreCaseAndActivoTrue(String ciudad);
 
 List<Propiedad> findByTipoInmuebleAndActivoTrue(String tipoInmueble);
 
 List<Propiedad> findByPrecioNocheLessThanEqualAndActivoTrue(BigDecimal precioMaximo);
}