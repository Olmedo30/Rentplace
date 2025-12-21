package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.gestores.GestorPropiedad;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GestorPropiedadTest {

    private GestorPropiedad gestorPropiedad;
    private TestPropiedadDAO propiedadDAO;

    @BeforeEach
    public void setUp() {
        propiedadDAO = new TestPropiedadDAO();
        gestorPropiedad = new GestorPropiedad();
        gestorPropiedad.propiedadDAO = propiedadDAO;
    }
    
    private static class TestPropiedadDAO implements PropiedadDAO {
        private Map<Long, Propiedad> propiedades = new HashMap<>();
        private Long idCounter = 1L;
        
        @Override
        public List<Propiedad> findByActivoTrue() {
            return propiedades.values().stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .collect(Collectors.toList());
        }
        
        @Override
        public List<String> findDistinctTipoInmuebleByActivoTrue() {
            return propiedades.values().stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .map(Propiedad::getTipoInmueble)
                .distinct()
                .collect(Collectors.toList());
        }
        
        @Override
        public List<Propiedad> buscarAvanzado(String ciudad, String tipoInmueble, BigDecimal precioMax, Boolean reservaInmediata) {
            return propiedades.values().stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .filter(p -> ciudad == null || (p.getCiudad() != null && p.getCiudad().contains(ciudad)))
                .filter(p -> tipoInmueble == null || tipoInmueble.equals(p.getTipoInmueble()))
                .filter(p -> precioMax == null || (p.getPrecioNoche() != null && p.getPrecioNoche().compareTo(precioMax) <= 0))
                .filter(p -> reservaInmediata == null || reservaInmediata.equals(p.getPermiteReservaInmediata()))
                .collect(Collectors.toList());
        }
        
        @Override
        public List<Propiedad> findByPropietarioIdAndActivoTrue(Long propietarioId) {
            return propiedades.values().stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .filter(p -> p.getPropietario() != null && p.getPropietario().getId().equals(propietarioId))
                .collect(Collectors.toList());
        }
        
        @Override
        public List<Propiedad> findByPrecioNocheBetweenAndActivoTrue(BigDecimal precioMin, BigDecimal precioMax) {
            return propiedades.values().stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .filter(p -> p.getPrecioNoche() != null)
                .filter(p -> p.getPrecioNoche().compareTo(precioMin) >= 0 && p.getPrecioNoche().compareTo(precioMax) <= 0)
                .collect(Collectors.toList());
        }
        
        public void addPropiedad(Propiedad p) {
            if (p.getId() == null) {
                p.setId(idCounter++);
            }
            propiedades.put(p.getId(), p);
        }
        
        @Override public Optional<Propiedad> findById(Long id) { return Optional.ofNullable(propiedades.get(id)); }
        @Override public List<String> findDistinctCiudadByActivoTrue() { return null; }
        @Override public List<Propiedad> findByCiudadContainingIgnoreCaseAndActivoTrue(String ciudad) { return null; }
        @Override public List<Propiedad> findByTipoInmuebleAndActivoTrue(String tipoInmueble) { return null; }
        @Override public List<Propiedad> findByPrecioNocheLessThanEqualAndActivoTrue(BigDecimal precioMax) { return null; }
        @Override public List<Propiedad> findByPermiteReservaInmediataAndActivoTrue(Boolean reservaInmediata) { return null; }
        @Override public List<Propiedad> findByPropietarioId(Long propietarioId) { return null; }
        @Override public List<Propiedad> findAll() { return null; }
        @Override public List<Propiedad> findAllById(Iterable<Long> ids) { return null; }
        @Override public long count() { return propiedades.size(); }
        @Override public void deleteById(Long id) { propiedades.remove(id); }
        @Override public void delete(Propiedad entity) {}
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Propiedad> entities) {}
        @Override public void deleteAll() { propiedades.clear(); }
        @Override public <S extends Propiedad> S save(S entity) { addPropiedad(entity); return entity; }
        @Override public <S extends Propiedad> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public boolean existsById(Long id) { return propiedades.containsKey(id); }
        @Override public void flush() {}
        @Override public <S extends Propiedad> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Propiedad> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<Propiedad> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Propiedad getOne(Long id) { return null; }
        @Override public Propiedad getById(Long id) { return propiedades.get(id); }
        @Override public Propiedad getReferenceById(Long id) { return propiedades.get(id); }
        @Override public <S extends Propiedad> Optional<S> findOne(org.springframework.data.domain.Example<S> example) { return Optional.empty(); }
        @Override public <S extends Propiedad> List<S> findAll(org.springframework.data.domain.Example<S> example) { return null; }
        @Override public <S extends Propiedad> List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) { return null; }
        @Override public <S extends Propiedad> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) { return null; }
        @Override public <S extends Propiedad> long count(org.springframework.data.domain.Example<S> example) { return 0; }
        @Override public <S extends Propiedad> boolean exists(org.springframework.data.domain.Example<S> example) { return false; }
        @Override public <S extends Propiedad, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Propiedad> findAll(org.springframework.data.domain.Sort sort) { return null; }
        @Override public org.springframework.data.domain.Page<Propiedad> findAll(org.springframework.data.domain.Pageable pageable) { return null; }
        @Override public List<Propiedad> findAll(org.springframework.data.jpa.domain.Specification<Propiedad> spec) { return null; }
        @Override public org.springframework.data.domain.Page<Propiedad> findAll(org.springframework.data.jpa.domain.Specification<Propiedad> spec, org.springframework.data.domain.Pageable pageable) { return null; }
        @Override public List<Propiedad> findAll(org.springframework.data.jpa.domain.Specification<Propiedad> spec, org.springframework.data.domain.Sort sort) { return null; }
        @Override public long count(org.springframework.data.jpa.domain.Specification<Propiedad> spec) { return 0; }
        @Override public boolean exists(org.springframework.data.jpa.domain.Specification<Propiedad> spec) { return false; }
        @Override public long delete(org.springframework.data.jpa.domain.Specification<Propiedad> spec) { return 0; }
        @Override public <S extends Propiedad, R> R findBy(org.springframework.data.jpa.domain.Specification<Propiedad> spec, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
    }

    @Test
    public void testBuscarPropiedadesActivas() {
        Propiedad propiedad1 = new Propiedad();
        propiedad1.setActivo(true);
        Propiedad propiedad2 = new Propiedad();
        propiedad2.setActivo(true);
        propiedadDAO.addPropiedad(propiedad1);
        propiedadDAO.addPropiedad(propiedad2);

        List<Propiedad> propiedadesActivas = gestorPropiedad.buscarPropiedadesActivas();

        assertEquals(2, propiedadesActivas.size());
    }

    @Test
    public void testObtenerTiposInmueblesActivos() {
        Propiedad p1 = new Propiedad();
        p1.setActivo(true);
        p1.setTipoInmueble("Apartamento");
        Propiedad p2 = new Propiedad();
        p2.setActivo(true);
        p2.setTipoInmueble("Casa");
        propiedadDAO.addPropiedad(p1);
        propiedadDAO.addPropiedad(p2);

        List<String> tipos = gestorPropiedad.obtenerTiposInmueblesActivos();

        assertEquals(2, tipos.size());
    }

    @Test
    public void testBuscarPropiedadesAvanzadas() {
        Propiedad propiedad = new Propiedad();
        propiedad.setActivo(true);
        propiedad.setCiudad("Madrid");
        propiedad.setTipoInmueble("Apartamento");
        propiedad.setPrecioNoche(BigDecimal.valueOf(1000));
        propiedad.setPermiteReservaInmediata(true);
        propiedadDAO.addPropiedad(propiedad);

        List<Propiedad> resultados = gestorPropiedad.buscarPropiedadesAvanzadas("Madrid", "Apartamento", BigDecimal.valueOf(1000), true);

        assertEquals(1, resultados.size());
    }

    @Test
    public void testBuscarPropiedadesActivasPorPropietario_ValidId() {
        es.uclm.rentplace.entity.Usuario propietario = new es.uclm.rentplace.entity.Usuario();
        propietario.setId(1L);
        Propiedad propiedad = new Propiedad();
        propiedad.setActivo(true);
        propiedad.setPropietario(propietario);
        propiedadDAO.addPropiedad(propiedad);

        List<Propiedad> propiedades = gestorPropiedad.buscarPropiedadesActivasPorPropietario(1L);

        assertEquals(1, propiedades.size());
    }

    @Test
    public void testBuscarPropiedadesActivasPorPropietario_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> gestorPropiedad.buscarPropiedadesActivasPorPropietario(0L));
    }

    @Test
    public void testBuscarPorRangoDePrecios_ValidRange() {
        Propiedad propiedad1 = new Propiedad();
        propiedad1.setActivo(true);
        propiedad1.setPrecioNoche(BigDecimal.valueOf(200));
        Propiedad propiedad2 = new Propiedad();
        propiedad2.setActivo(true);
        propiedad2.setPrecioNoche(BigDecimal.valueOf(400));
        propiedadDAO.addPropiedad(propiedad1);
        propiedadDAO.addPropiedad(propiedad2);

        List<Propiedad> propiedades = gestorPropiedad.buscarPorRangoDePrecios(BigDecimal.valueOf(100), BigDecimal.valueOf(500));

        assertEquals(2, propiedades.size());
    }

    @Test
    public void testBuscarPorRangoDePrecios_InvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> gestorPropiedad.buscarPorRangoDePrecios(BigDecimal.valueOf(500), BigDecimal.valueOf(100)));
        assertThrows(IllegalArgumentException.class, () -> gestorPropiedad.buscarPorRangoDePrecios(null, BigDecimal.valueOf(500)));
        assertThrows(IllegalArgumentException.class, () -> gestorPropiedad.buscarPorRangoDePrecios(BigDecimal.valueOf(100), null));
    }
}
