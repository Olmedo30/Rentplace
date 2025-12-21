package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.entity.Disponibilidad;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.gestores.GestorDisponibilidad;
import es.uclm.rentplace.persistence.DisponibilidadDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GestorDisponibilidadTest {

    private GestorDisponibilidad gestorDisponibilidad;
    private TestDisponibilidadDAO disponibilidadDAO;

    @BeforeEach
    public void setUp() {
        disponibilidadDAO = new TestDisponibilidadDAO();
        gestorDisponibilidad = new GestorDisponibilidad();
        gestorDisponibilidad.disponibilidadDAO = disponibilidadDAO;
    }
    
    private static class TestDisponibilidadDAO implements DisponibilidadDAO {
        private Map<Long, Disponibilidad> disponibilidades = new HashMap<>();
        private Long idCounter = 1L;
        
        @Override
        public List<Disponibilidad> findByPropiedadId(Long propiedadId) {
            List<Disponibilidad> result = new ArrayList<>();
            for (Disponibilidad d : disponibilidades.values()) {
                if (d.getPropiedad().getId().equals(propiedadId)) {
                    result.add(d);
                }
            }
            return result;
        }
        
        @Override
        public List<Disponibilidad> findByPropiedadIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                Long propiedadId, LocalDate fechaInicio, LocalDate fechaFin) {
            List<Disponibilidad> result = new ArrayList<>();
            for (Disponibilidad d : disponibilidades.values()) {
                if (d.getPropiedad().getId().equals(propiedadId) &&
                    !d.getFechaInicio().isAfter(fechaInicio) &&
                    !d.getFechaFin().isBefore(fechaFin)) {
                    result.add(d);
                }
            }
            return result;
        }
        
        @Override
        public List<Disponibilidad> Disponibilidades_Sopalan(Long propiedadId, LocalDate fechaFin, LocalDate fechaInicio) {
            return findByPropiedadIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(propiedadId, fechaInicio, fechaFin);
        }
        
        @Override
        public <S extends Disponibilidad> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idCounter++);
            }
            disponibilidades.put(entity.getId(), entity);
            return entity;
        }
        
        @Override public Optional<Disponibilidad> findById(Long id) { return Optional.ofNullable(disponibilidades.get(id)); }
        @Override public List<Disponibilidad> findAll() { return new ArrayList<>(disponibilidades.values()); }
        @Override public List<Disponibilidad> findAllById(Iterable<Long> ids) { return null; }
        @Override public long count() { return disponibilidades.size(); }
        @Override public void deleteById(Long id) { disponibilidades.remove(id); }
        @Override public void delete(Disponibilidad entity) { disponibilidades.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Disponibilidad> entities) {}
        @Override public void deleteAll() { disponibilidades.clear(); }
        @Override public <S extends Disponibilidad> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public boolean existsById(Long id) { return disponibilidades.containsKey(id); }
        @Override public void flush() {}
        @Override public <S extends Disponibilidad> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Disponibilidad> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<Disponibilidad> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Disponibilidad getOne(Long id) { return null; }
        @Override public Disponibilidad getById(Long id) { return disponibilidades.get(id); }
        @Override public Disponibilidad getReferenceById(Long id) { return disponibilidades.get(id); }
        @Override public <S extends Disponibilidad> Optional<S> findOne(org.springframework.data.domain.Example<S> example) { return Optional.empty(); }
        @Override public <S extends Disponibilidad> List<S> findAll(org.springframework.data.domain.Example<S> example) { return null; }
        @Override public <S extends Disponibilidad> List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) { return null; }
        @Override public <S extends Disponibilidad> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) { return null; }
        @Override public <S extends Disponibilidad> long count(org.springframework.data.domain.Example<S> example) { return 0; }
        @Override public <S extends Disponibilidad> boolean exists(org.springframework.data.domain.Example<S> example) { return false; }
        @Override public <S extends Disponibilidad, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Disponibilidad> findAll(org.springframework.data.domain.Sort sort) { return null; }
        @Override public org.springframework.data.domain.Page<Disponibilidad> findAll(org.springframework.data.domain.Pageable pageable) { return null; }
    }

    @Test
    public void testBuscarDisponibilidadesPorPropiedad_ValidID() {
        Propiedad propiedad = new Propiedad();
        propiedad.setId(1L);
        Disponibilidad disp1 = new Disponibilidad(propiedad, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15), true, BigDecimal.valueOf(100));
        Disponibilidad disp2 = new Disponibilidad(propiedad, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 15), true, BigDecimal.valueOf(200));
        disponibilidadDAO.save(disp1);
        disponibilidadDAO.save(disp2);

        List<Disponibilidad> result = gestorDisponibilidad.buscarDisponibilidadesPorPropiedad(1L);

        assertEquals(2, result.size());
    }

    @Test
    public void testBuscarDisponibilidadesPorPropiedad_InvalidID() {
        assertThrows(IllegalArgumentException.class, () -> gestorDisponibilidad.buscarDisponibilidadesPorPropiedad(0L));
    }

    @Test
    public void testBuscarDisponibilidadesEnIntervalo_ValidParams() {
        Propiedad propiedad = new Propiedad();
        propiedad.setId(1L);
        Disponibilidad disp = new Disponibilidad(propiedad, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15), true, BigDecimal.valueOf(100));
        disponibilidadDAO.save(disp);

        List<Disponibilidad> result = gestorDisponibilidad.buscarDisponibilidadesEnIntervalo(
                1L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10));

        assertEquals(1, result.size());
    }

    @Test
    public void testBuscarDisponibilidadesEnIntervalo_InvalidDates() {
        assertThrows(IllegalArgumentException.class, () -> gestorDisponibilidad.buscarDisponibilidadesEnIntervalo(
                1L, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 1)));
    }

    @Test
    public void testCrearDisponibilidad_ValidParams() {
        Propiedad propiedad = new Propiedad();
        propiedad.setId(1L);

        Disponibilidad result = gestorDisponibilidad.crearDisponibilidad(
                propiedad, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15), true, BigDecimal.valueOf(100));

        assertNotNull(result);
    }

    @Test
    public void testCrearDisponibilidad_InvalidParams() {
        Propiedad propiedad = new Propiedad();
        propiedad.setId(1L);
        assertThrows(IllegalArgumentException.class, () -> gestorDisponibilidad.crearDisponibilidad(
                propiedad, LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 1), true, BigDecimal.valueOf(100)));
    }
}
