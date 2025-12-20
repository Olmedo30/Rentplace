package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.entity.ListaDeseos;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.gestores.GestorListaDeseos;
import es.uclm.rentplace.persistence.ListaDeseosDAO;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GestorListaDeseosTest {

    private GestorListaDeseos gestorListaDeseos;
    private ListaDeseosDAO listaDeseosDAO;
    private PropiedadDAO propiedadDAO;

    @BeforeEach
    public void setUp() {
        listaDeseosDAO = mock(ListaDeseosDAO.class);
        propiedadDAO = mock(PropiedadDAO.class);
        gestorListaDeseos = new GestorListaDeseos();
        gestorListaDeseos.listaDeseosDAO = listaDeseosDAO;
        gestorListaDeseos.propiedadDAO = propiedadDAO;
    }

    // TESTS para obtenerListaDeseosPorUsuario 

    @Test
    public void testObtenerListaDeseosPorUsuario_Existe() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));

        ListaDeseos resultado = gestorListaDeseos.obtenerListaDeseosPorUsuario(1L);

        assertNotNull(resultado);
        assertEquals(lista, resultado);
    }

    @Test
    public void testObtenerListaDeseosPorUsuario_NoExiste() {
        when(listaDeseosDAO.findByUsuarioId(999L)).thenReturn(Optional.empty());

        ListaDeseos resultado = gestorListaDeseos.obtenerListaDeseosPorUsuario(999L);

        assertNull(resultado);
    }

    @Test
    public void testObtenerListaDeseosPorUsuario_IdInvalido() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.obtenerListaDeseosPorUsuario(0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.obtenerListaDeseosPorUsuario(-1L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.obtenerListaDeseosPorUsuario(null));
    }

    //  TESTS para usuarioTieneListaDeseos 

    @Test
    public void testUsuarioTieneListaDeseos_Existe() {
        when(listaDeseosDAO.existsByUsuarioId(1L)).thenReturn(true);

        boolean resultado = gestorListaDeseos.usuarioTieneListaDeseos(1L);

        assertTrue(resultado);
    }

    @Test
    public void testUsuarioTieneListaDeseos_NoExiste() {
        when(listaDeseosDAO.existsByUsuarioId(999L)).thenReturn(false);

        boolean resultado = gestorListaDeseos.usuarioTieneListaDeseos(999L);

        assertFalse(resultado);
    }

    @Test
    public void testUsuarioTieneListaDeseos_IdInvalido() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.usuarioTieneListaDeseos(0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.usuarioTieneListaDeseos(-1L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.usuarioTieneListaDeseos(null));
    }

    //  TESTS para crearListaDeseos 

    @Test
    public void testCrearListaDeseos_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos nuevaLista = new ListaDeseos(usuario);
        
        when(listaDeseosDAO.existsByUsuarioId(1L)).thenReturn(false);
        when(listaDeseosDAO.save(any(ListaDeseos.class))).thenReturn(nuevaLista);

        ListaDeseos resultado = gestorListaDeseos.crearListaDeseos(usuario);

        assertNotNull(resultado);
        verify(listaDeseosDAO, times(1)).save(any(ListaDeseos.class));
    }

    @Test
    public void testCrearListaDeseos_UsuarioNulo() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.crearListaDeseos(null));
    }

    @Test
    public void testCrearListaDeseos_UsuarioSinId() {
        Usuario usuario = new Usuario();
        usuario.setId(null);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.crearListaDeseos(usuario));
    }

    @Test
    public void testCrearListaDeseos_UsuarioIdInvalido() {
        Usuario usuario = new Usuario();
        usuario.setId(0L);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.crearListaDeseos(usuario));
    }

    @Test
    public void testCrearListaDeseos_YaExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        
        when(listaDeseosDAO.existsByUsuarioId(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.crearListaDeseos(usuario));
    }

    // TESTS para agregarPropiedadALista 

    @Test
    public void testAgregarPropiedadALista_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        Propiedad propiedad = new Propiedad();
        propiedad.setId(10L);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));
        when(propiedadDAO.findById(10L)).thenReturn(Optional.of(propiedad));
        when(listaDeseosDAO.save(any(ListaDeseos.class))).thenReturn(lista);

        boolean resultado = gestorListaDeseos.agregarPropiedadALista(1L, 10L);

        assertTrue(resultado);
        verify(listaDeseosDAO, times(1)).save(lista);
    }

    @Test
    public void testAgregarPropiedadALista_ListaNoExiste() {
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.empty());

        boolean resultado = gestorListaDeseos.agregarPropiedadALista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testAgregarPropiedadALista_PropiedadNoExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));
        when(propiedadDAO.findById(10L)).thenReturn(Optional.empty());

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
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));
        when(propiedadDAO.findById(10L)).thenReturn(Optional.of(propiedad));

        boolean resultado = gestorListaDeseos.agregarPropiedadALista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testAgregarPropiedadALista_IdsInvalidos() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.agregarPropiedadALista(0L, 10L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.agregarPropiedadALista(1L, 0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.agregarPropiedadALista(null, 10L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.agregarPropiedadALista(1L, null));
    }

    //  TESTS para eliminarPropiedadDeLista 

    @Test
    public void testEliminarPropiedadDeLista_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad propiedad = new Propiedad();
        propiedad.setId(10L);
        ListaDeseos lista = new ListaDeseos(usuario);
        lista.agregarPropiedad(propiedad);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));
        when(propiedadDAO.findById(10L)).thenReturn(Optional.of(propiedad));
        when(listaDeseosDAO.save(any(ListaDeseos.class))).thenReturn(lista);

        boolean resultado = gestorListaDeseos.eliminarPropiedadDeLista(1L, 10L);

        assertTrue(resultado);
        verify(listaDeseosDAO, times(1)).save(lista);
    }

    @Test
    public void testEliminarPropiedadDeLista_ListaNoExiste() {
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.empty());

        boolean resultado = gestorListaDeseos.eliminarPropiedadDeLista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testEliminarPropiedadDeLista_PropiedadNoExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));
        when(propiedadDAO.findById(10L)).thenReturn(Optional.empty());

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
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));
        when(propiedadDAO.findById(10L)).thenReturn(Optional.of(propiedad));

        boolean resultado = gestorListaDeseos.eliminarPropiedadDeLista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testEliminarPropiedadDeLista_IdsInvalidos() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.eliminarPropiedadDeLista(0L, 10L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.eliminarPropiedadDeLista(1L, 0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.eliminarPropiedadDeLista(null, 10L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.eliminarPropiedadDeLista(1L, null));
    }

    //  TESTS para obtenerPropiedadesDeLista 

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
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));

        List<Propiedad> resultado = gestorListaDeseos.obtenerPropiedadesDeLista(1L);

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(prop1));
        assertTrue(resultado.contains(prop2));
    }

    @Test
    public void testObtenerPropiedadesDeLista_ListaVacia() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));

        List<Propiedad> resultado = gestorListaDeseos.obtenerPropiedadesDeLista(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testObtenerPropiedadesDeLista_ListaNoExiste() {
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.empty());

        List<Propiedad> resultado = gestorListaDeseos.obtenerPropiedadesDeLista(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testObtenerPropiedadesDeLista_IdInvalido() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.obtenerPropiedadesDeLista(0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.obtenerPropiedadesDeLista(null));
    }

    //  TESTS para propiedadEstaEnLista

    @Test
    public void testPropiedadEstaEnLista_SiEsta() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad propiedad = new Propiedad();
        propiedad.setId(10L);
        ListaDeseos lista = new ListaDeseos(usuario);
        lista.agregarPropiedad(propiedad);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));

        boolean resultado = gestorListaDeseos.propiedadEstaEnLista(1L, 10L);

        assertTrue(resultado);
    }

    @Test
    public void testPropiedadEstaEnLista_NoEsta() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));

        boolean resultado = gestorListaDeseos.propiedadEstaEnLista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testPropiedadEstaEnLista_ListaNoExiste() {
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.empty());

        boolean resultado = gestorListaDeseos.propiedadEstaEnLista(1L, 10L);

        assertFalse(resultado);
    }

    @Test
    public void testPropiedadEstaEnLista_IdsInvalidos() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.propiedadEstaEnLista(0L, 10L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.propiedadEstaEnLista(1L, 0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.propiedadEstaEnLista(null, 10L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.propiedadEstaEnLista(1L, null));
    }

    // TESTS para contarPropiedadesEnLista 
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
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));

        int resultado = gestorListaDeseos.contarPropiedadesEnLista(1L);

        assertEquals(2, resultado);
    }

    @Test
    public void testContarPropiedadesEnLista_ListaVacia() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        ListaDeseos lista = new ListaDeseos(usuario);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));

        int resultado = gestorListaDeseos.contarPropiedadesEnLista(1L);

        assertEquals(0, resultado);
    }

    @Test
    public void testContarPropiedadesEnLista_ListaNoExiste() {
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.empty());

        int resultado = gestorListaDeseos.contarPropiedadesEnLista(1L);

        assertEquals(0, resultado);
    }

    @Test
    public void testContarPropiedadesEnLista_IdInvalido() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.contarPropiedadesEnLista(0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.contarPropiedadesEnLista(null));
    }

    //  TESTS para limpiarListaDeseos 

    @Test
    public void testLimpiarListaDeseos_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Propiedad prop1 = new Propiedad();
        prop1.setId(10L);
        ListaDeseos lista = new ListaDeseos(usuario);
        lista.agregarPropiedad(prop1);
        
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.of(lista));
        when(listaDeseosDAO.save(any(ListaDeseos.class))).thenReturn(lista);

        boolean resultado = gestorListaDeseos.limpiarListaDeseos(1L);

        assertTrue(resultado);
        assertTrue(lista.getPropiedades().isEmpty());
        verify(listaDeseosDAO, times(1)).save(lista);
    }

    @Test
    public void testLimpiarListaDeseos_ListaNoExiste() {
        when(listaDeseosDAO.findByUsuarioId(1L)).thenReturn(Optional.empty());

        boolean resultado = gestorListaDeseos.limpiarListaDeseos(1L);

        assertFalse(resultado);
    }

    @Test
    public void testLimpiarListaDeseos_IdInvalido() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.limpiarListaDeseos(0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.limpiarListaDeseos(-1L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorListaDeseos.limpiarListaDeseos(null));
    }
}
