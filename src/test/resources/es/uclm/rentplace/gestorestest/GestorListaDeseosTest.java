package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.entity.ListaDeseos;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.gestores.GestorListaDeseos;
import es.uclm.rentplace.persistence.ListaDeseosDAO;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GestorListaDeseosTest {

    private GestorListaDeseos gestorListaDeseos;
    private TestListaDeseosDAO listaDeseosDAO;
    private TestPropiedadDAO propiedadDAO;

    @BeforeEach
    public void setUp() {
        listaDeseosDAO = new TestListaDeseosDAO();
        propiedadDAO = new TestPropiedadDAO();
        gestorListaDeseos = new GestorListaDeseos();
        gestorListaDeseos.listaDeseosDAO = listaDeseosDAO;
        gestorListaDeseos.propiedadDAO = propiedadDAO;
    }
    
    private static class TestListaDeseosDAO implements ListaDeseosDAO {
        private Map<Long, ListaDeseos> listas = new HashMap<>();
        private Long idCounter = 1L;
        
        @Override
        public Optional<ListaDeseos> findByUsuarioId(Long usuarioId) {
            return listas.values().stream()
                .filter(l -> l.getUsuario().getId().equals(usuarioId))
                .findFirst();
        }
        
        @Override
        public boolean existsByUsuarioId(Long usuarioId) {
            return findByUsuarioId(usuarioId).isPresent();
        }
        
        @Override
        public <S extends ListaDeseos> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idCounter++);
            }
            listas.put(entity.getId(), entity);
            return entity;
        }
        
        @Override public Optional<ListaDeseos> findById(Long id) { return Optional.ofNullable(listas.get(id)); }
        @Override public List<ListaDeseos> findAll() { return new ArrayList<>(listas.values()); }
        @Override public List<ListaDeseos> findAllById(Iterable<Long> ids) { return null; }
        @Override public long count() { return listas.size(); }
        @Override public void deleteById(Long id) { listas.remove(id); }
        @Override public void delete(ListaDeseos entity) { listas.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends ListaDeseos> entities) {}
        @Override public void deleteAll() { listas.clear(); }
        @Override public <S extends ListaDeseos> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public boolean existsById(Long id) { return listas.containsKey(id); }
        @Override public void flush() {}
        @Override public <S extends ListaDeseos> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends ListaDeseos> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<ListaDeseos> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public ListaDeseos getOne(Long id) { return null; }
        @Override public ListaDeseos getById(Long id) { return listas.get(id); }
        @Override public ListaDeseos getReferenceById(Long id) { return listas.get(id); }
        @Override public <S extends ListaDeseos> Optional<S> findOne(org.springframework.data.domain.Example<S> example) { return Optional.empty(); }
        @Override public <S extends ListaDeseos> List<S> findAll(org.springframework.data.domain.Example<S> example) { return null; }
        @Override public <S extends ListaDeseos> List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) { return null; }
        @Override public <S extends ListaDeseos> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) { return null; }
        @Override public <S extends ListaDeseos> long count(org.springframework.data.domain.Example<S> example) { return 0; }
        @Override public <S extends ListaDeseos> boolean exists(org.springframework.data.domain.Example<S> example) { return false; }
        @Override public <S extends ListaDeseos, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<ListaDeseos> findAll(org.springframework.data.domain.Sort sort) { return null; }
        @Override public org.springframework.data.domain.Page<ListaDeseos> findAll(org.springframework.data.domain.Pageable pageable) { return null; }
    }
    
    private static class TestPropiedadDAO implements PropiedadDAO {
        private Map<Long, Propiedad> propiedades = new HashMap<>();
        
        public void addPropiedad(Propiedad p) {
            propiedades.put(p.getId(), p);
        }
        
        @Override public Optional<Propiedad> findById(Long id) { return Optional.ofNullable(propiedades.get(id)); }
        @Override public List<Propiedad> findByActivoTrue() { return null; }
        @Override public List<String> findDistinctTipoInmuebleByActivoTrue() { return null; }
        @Override public List<String> findDistinctCiudadByActivoTrue() { return null; }
        @Override public List<Propiedad> findByCiudadContainingIgnoreCaseAndActivoTrue(String ciudad) { return null; }
        @Override public List<Propiedad> findByTipoInmuebleAndActivoTrue(String tipoInmueble) { return null; }
        @Override public List<Propiedad> findByPrecioNocheLessThanEqualAndActivoTrue(java.math.BigDecimal precioMax) { return null; }
        @Override public List<Propiedad> findByPermiteReservaInmediataAndActivoTrue(Boolean reservaInmediata) { return null; }
        @Override public List<Propiedad> findByPropietarioIdAndActivoTrue(Long propietarioId) { return null; }
        @Override public List<Propiedad> findByPropietarioId(Long propietarioId) { return null; }
        @Override public List<Propiedad> buscarAvanzado(String ciudad, String tipoInmueble, java.math.BigDecimal precioMax, Boolean reservaInmediata) { return null; }
        @Override public List<Propiedad> findByPrecioNocheBetweenAndActivoTrue(java.math.BigDecimal precioMin, java.math.BigDecimal precioMax) { return null; }
        @Override public List<Propiedad> findAll() { return null; }
        @Override public List<Propiedad> findAllById(Iterable<Long> ids) { return null; }
        @Override public long count() { return 0; }
        @Override public void deleteById(Long id) {}
        @Override public void delete(Propiedad entity) {}
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Propiedad> entities) {}
        @Override public void deleteAll() {}
        @Override public <S extends Propiedad> S save(S entity) { return null; }
        @Override public <S extends Propiedad> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public boolean existsById(Long id) { return false; }
        @Override public void flush() {}
        @Override public <S extends Propiedad> S saveAndFlush(S entity) { return null; }
        @Override public <S extends Propiedad> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<Propiedad> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Propiedad getOne(Long id) { return null; }
        @Override public Propiedad getById(Long id) { return null; }
        @Override public Propiedad getReferenceById(Long id) { return null; }
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
    public void testObtenerListaDeseosPorUsuario_Existe() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);

        ListaDeseos resultado = gestorListaDeseos.obtenerListaDeseosPorUsuario(1L);

        assertNotNull(resultado);
    }

    @Test
    public void testObtenerListaDeseosPorUsuario_NoExiste() {
        ListaDeseos resultado = gestorListaDeseos.obtenerListaDeseosPorUsuario(999L);
        assertNull(resultado);
    }

    @Test
    public void testObtenerListaDeseosPorUsuario_IdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.obtenerListaDeseosPorUsuario(0L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.obtenerListaDeseosPorUsuario(-1L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.obtenerListaDeseosPorUsuario(null));
    }

    @Test
    public void testUsuarioTieneListaDeseos_Existe() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);

        boolean resultado = gestorListaDeseos.usuarioTieneListaDeseos(1L);

        assertTrue(resultado);
    }

    @Test
    public void testUsuarioTieneListaDeseos_NoExiste() {
        boolean resultado = gestorListaDeseos.usuarioTieneListaDeseos(999L);
        assertFalse(resultado);
    }

    @Test
    public void testUsuarioTieneListaDeseos_IdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.usuarioTieneListaDeseos(0L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.usuarioTieneListaDeseos(-1L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.usuarioTieneListaDeseos(null));
    }

    @Test
    public void testCrearListaDeseos_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        ListaDeseos resultado = gestorListaDeseos.crearListaDeseos(usuario);

        assertNotNull(resultado);
    }

    @Test
    public void testCrearListaDeseos_UsuarioNulo() {
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.crearListaDeseos(null));
    }

    @Test
    public void testCrearListaDeseos_UsuarioSinId() {
        Usuario usuario = new Usuario();
        usuario.setId(null);
        
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.crearListaDeseos(usuario));
    }

    @Test
    public void testCrearListaDeseos_UsuarioIdInvalido() {
        Usuario usuario = new Usuario();
        usuario.setId(0L);
        
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.crearListaDeseos(usuario));
    }

    @Test
    public void testCrearListaDeseos_YaExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);

        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.crearListaDeseos(usuario));
    }

    @Test
    public void testAgregarPropiedadALista_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);
        
        Propiedad propiedad = new Propiedad();
        propiedad.setId(10L);
        propiedadDAO.addPropiedad(propiedad);

        boolean resultado = gestorListaDeseos.agregarPropiedadALista(1L, 10L);

        assertTrue(resultado);
    }

    @Test
    public void testAgregarPropiedadALista_ListaNoExiste() {
        Propiedad propiedad = new Propiedad();
        propiedad.setId(10L);
        propiedadDAO.addPropiedad(propiedad);

        boolean resultado = gestorListaDeseos.agregarPropiedadALista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testAgregarPropiedadALista_PropiedadNoExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);

        boolean resultado = gestorListaDeseos.agregarPropiedadALista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testAgregarPropiedadALista_PropiedadYaEnLista() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad propiedad = new Propiedad();
        propiedad.setId(10L);
        ListaDeseos lista = new ListaDeseos(usuario);
        lista.agregarPropiedad(propiedad);
        listaDeseosDAO.save(lista);
        propiedadDAO.addPropiedad(propiedad);

        boolean resultado = gestorListaDeseos.agregarPropiedadALista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testAgregarPropiedadALista_IdsInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.agregarPropiedadALista(0L, 10L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.agregarPropiedadALista(1L, 0L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.agregarPropiedadALista(null, 10L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.agregarPropiedadALista(1L, null));
    }

    @Test
    public void testEliminarPropiedadDeLista_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad propiedad = new Propiedad();
        propiedad.setId(10L);
        ListaDeseos lista = new ListaDeseos(usuario);
        lista.agregarPropiedad(propiedad);
        listaDeseosDAO.save(lista);
        propiedadDAO.addPropiedad(propiedad);

        boolean resultado = gestorListaDeseos.eliminarPropiedadDeLista(1L, 10L);

        assertTrue(resultado);
    }

    @Test
    public void testEliminarPropiedadDeLista_ListaNoExiste() {
        boolean resultado = gestorListaDeseos.eliminarPropiedadDeLista(1L, 10L);
        assertFalse(resultado);
    }

    @Test
    public void testEliminarPropiedadDeLista_PropiedadNoExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);

        boolean resultado = gestorListaDeseos.eliminarPropiedadDeLista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testEliminarPropiedadDeLista_PropiedadNoEnLista() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad propiedad = new Propiedad();
        propiedad.setId(10L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);
        propiedadDAO.addPropiedad(propiedad);

        boolean resultado = gestorListaDeseos.eliminarPropiedadDeLista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testEliminarPropiedadDeLista_IdsInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.eliminarPropiedadDeLista(0L, 10L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.eliminarPropiedadDeLista(1L, 0L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.eliminarPropiedadDeLista(null, 10L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.eliminarPropiedadDeLista(1L, null));
    }

    @Test
    public void testObtenerPropiedadesDeLista_ConPropiedades() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad prop1 = new Propiedad();
        prop1.setId(10L);
        Propiedad prop2 = new Propiedad();
        prop2.setId(20L);
        ListaDeseos lista = new ListaDeseos(usuario);
        lista.agregarPropiedad(prop1);
        lista.agregarPropiedad(prop2);
        listaDeseosDAO.save(lista);

        List<Propiedad> resultado = gestorListaDeseos.obtenerPropiedadesDeLista(1L);

        assertEquals(2, resultado.size());
    }

    @Test
    public void testObtenerPropiedadesDeLista_ListaVacia() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);

        List<Propiedad> resultado = gestorListaDeseos.obtenerPropiedadesDeLista(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testObtenerPropiedadesDeLista_ListaNoExiste() {
        List<Propiedad> resultado = gestorListaDeseos.obtenerPropiedadesDeLista(1L);
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testObtenerPropiedadesDeLista_IdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.obtenerPropiedadesDeLista(0L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.obtenerPropiedadesDeLista(null));
    }

    @Test
    public void testPropiedadEstaEnLista_SiEsta() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad propiedad = new Propiedad();
        propiedad.setId(10L);
        ListaDeseos lista = new ListaDeseos(usuario);
        lista.agregarPropiedad(propiedad);
        listaDeseosDAO.save(lista);

        boolean resultado = gestorListaDeseos.propiedadEstaEnLista(1L, 10L);

        assertTrue(resultado);
    }

    @Test
    public void testPropiedadEstaEnLista_NoEsta() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);

        boolean resultado = gestorListaDeseos.propiedadEstaEnLista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testPropiedadEstaEnLista_ListaNoExiste() {
        boolean resultado = gestorListaDeseos.propiedadEstaEnLista(1L, 10L);
        assertFalse(resultado);
    }

    @Test
    public void testPropiedadEstaEnLista_IdsInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.propiedadEstaEnLista(0L, 10L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.propiedadEstaEnLista(1L, 0L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.propiedadEstaEnLista(null, 10L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.propiedadEstaEnLista(1L, null));
    }

    @Test
    public void testContarPropiedadesEnLista_ConPropiedades() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad prop1 = new Propiedad();
        prop1.setId(10L);
        Propiedad prop2 = new Propiedad();
        prop2.setId(20L);
        ListaDeseos lista = new ListaDeseos(usuario);
        lista.agregarPropiedad(prop1);
        lista.agregarPropiedad(prop2);
        listaDeseosDAO.save(lista);

        int resultado = gestorListaDeseos.contarPropiedadesEnLista(1L);

        assertEquals(2, resultado);
    }

    @Test
    public void testContarPropiedadesEnLista_ListaVacia() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        listaDeseosDAO.save(lista);

        int resultado = gestorListaDeseos.contarPropiedadesEnLista(1L);

        assertEquals(0, resultado);
    }

    @Test
    public void testContarPropiedadesEnLista_ListaNoExiste() {
        int resultado = gestorListaDeseos.contarPropiedadesEnLista(1L);
        assertEquals(0, resultado);
    }

    @Test
    public void testContarPropiedadesEnLista_IdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.contarPropiedadesEnLista(0L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.contarPropiedadesEnLista(null));
    }

    @Test
    public void testLimpiarListaDeseos_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad prop1 = new Propiedad();
        prop1.setId(10L);
        ListaDeseos lista = new ListaDeseos(usuario);
        lista.agregarPropiedad(prop1);
        listaDeseosDAO.save(lista);

        boolean resultado = gestorListaDeseos.limpiarListaDeseos(1L);

        assertTrue(resultado);
        assertTrue(lista.getPropiedades().isEmpty());
    }

    @Test
    public void testLimpiarListaDeseos_ListaNoExiste() {
        boolean resultado = gestorListaDeseos.limpiarListaDeseos(1L);
        assertFalse(resultado);
    }

    @Test
    public void testLimpiarListaDeseos_IdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.limpiarListaDeseos(0L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.limpiarListaDeseos(-1L));
        assertThrows(IllegalArgumentException.class, () -> gestorListaDeseos.limpiarListaDeseos(null));
    }
}
