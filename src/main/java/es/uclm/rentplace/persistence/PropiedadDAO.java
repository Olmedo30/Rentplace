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

    // Búsquedas simples
    List<Propiedad> findByCiudad(String ciudad);
    List<Propiedad> findByTipoInmueble(String tipoInmueble);
    List<Propiedad> findByPropietarioId(Long propietarioId);
    List<Propiedad> findByActivoTrue();
    List<Propiedad> findByPrecioNocheLessThanEqual(BigDecimal precioMaximo);

    // Buscamos por campos del propietario (Propiedad -> Propietario -> email/nombre)
    List<Propiedad> findByPropietarioEmail(String email);
    List<Propiedad> findByPropietarioNombre(String nombre);

    /**
     * Consulta nativa para obtener propiedades disponibles en el intervalo [start, end].
     *
     * IMPORTANTE (Derby):
     *  - Si tu columna 'disponible' está definida como BOOLEAN en la BD, deja `d.disponible = true`.
     *  - Si en Derby la columna 'disponible' es SMALLINT/INT (0/1), sustituye `d.disponible = true`
     *    por `d.disponible = 1` (he incluido la alternativa comentada más abajo).
     *
     * También verifica el tipo y valores del campo 'estado' en la tabla 'reservas' (VARCHAR vs numérico).
     */
    @Query(value = "" +
            "SELECT p.* FROM propiedades p " +
            "WHERE EXISTS ( " +
            "   SELECT 1 FROM disponibilidades d " +
            "   WHERE d.propiedad_id = p.propiedades_id " +
            "     AND d.disponible = true " +                // <-- si es SMALLINT en Derby, cambiar a = 1
            "     AND d.fecha_inicio <= :start " +
            "     AND d.fecha_fin >= :end " +
            ") " +
            "AND NOT EXISTS ( " +
            "   SELECT 1 FROM reservas r " +
            "   WHERE r.propiedad_id = p.propiedades_id " +
            "     AND r.estado IN ('pendiente','confirmada') " +
            "     AND r.fecha_inicio <= :end " +
            "     AND r.fecha_fin >= :start " +
            ")",
            nativeQuery = true)
    List<Propiedad> findAvailablePropertiesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

 
}