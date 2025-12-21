package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.gestores.GestorListaDeseos;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GestorListaDeseosTest {

    // Simuladores simples de usuario y propiedad
    private final Map<Long, GestorListaDeseos.Usuario> usuarios = new ConcurrentHashMap<>();
    private final Map<Long, GestorListaDeseos.Propiedad> propiedades = new ConcurrentHashMap<>();

    private GestorListaDeseos.Usuario crearUsuario(Long id, GestorListaDeseos.Usuario.Rol rol) {
        var u = new GestorListaDeseos.Usuario(id, rol);
        usuarios.put(id, u);
        return u;
    }

    private GestorListaDeseos.Propiedad crearPropiedad(Long id) {
        var p = new GestorListaDeseos.Propiedad(id);
        propiedades.put(id, p);
        return p;
    }

    @Test
    public void agregarPropiedadALista_exito() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearUsuario(1L, GestorListaDeseos.Usuario.Rol.INQUILINO);
        crearPropiedad(10L);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        assertTrue(service.agregarPropiedadALista(1L, 10L));
        assertTrue(service.estaEnListaDeDeseos(1L, 10L));

        var lista = service.obtenerPropiedadesDeLista(1L);
        assertEquals(1, lista.size());
        assertEquals(10L, lista.get(0).getId());
    }

    @Test
    public void agregarPropiedadALista_falla_noInquilino() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearUsuario(2L, GestorListaDeseos.Usuario.Rol.PROPIETARIO);
        crearPropiedad(20L);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        assertFalse(service.agregarPropiedadALista(2L, 20L));
        assertFalse(service.estaEnListaDeDeseos(2L, 20L));
    }

    @Test
    public void agregarPropiedadALista_falla_usuarioInexistente() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearPropiedad(30L);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        assertFalse(service.agregarPropiedadALista(999L, 30L));
    }

    @Test
    public void agregarPropiedadALista_falla_propiedadInexistente() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearUsuario(3L, GestorListaDeseos.Usuario.Rol.INQUILINO);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        assertFalse(service.agregarPropiedadALista(3L, 999L));
    }

    @Test
    public void agregarPropiedadALista_duplicado_ignorado() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearUsuario(4L, GestorListaDeseos.Usuario.Rol.INQUILINO);
        crearPropiedad(40L);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        assertTrue(service.agregarPropiedadALista(4L, 40L));
        assertTrue(service.agregarPropiedadALista(4L, 40L)); // segunda vez

        var lista = service.obtenerPropiedadesDeLista(4L);
        assertEquals(1, lista.size());
    }

    @Test
    public void eliminarPropiedadDeLista_exito() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearUsuario(5L, GestorListaDeseos.Usuario.Rol.INQUILINO);
        crearPropiedad(50L);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        service.agregarPropiedadALista(5L, 50L);
        assertTrue(service.estaEnListaDeDeseos(5L, 50L));

        assertTrue(service.eliminarPropiedadDeLista(5L, 50L));
        assertFalse(service.estaEnListaDeDeseos(5L, 50L));
        assertTrue(service.obtenerPropiedadesDeLista(5L).isEmpty());
    }

    @Test
    public void eliminarPropiedadDeLista_falla_noEstaEnLista() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearUsuario(6L, GestorListaDeseos.Usuario.Rol.INQUILINO);
        crearPropiedad(60L);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        // No se ha agregado → eliminar debe fallar
        assertFalse(service.eliminarPropiedadDeLista(6L, 60L));
    }

    @Test
    public void eliminarPropiedadDeLista_falla_usuarioNoInquilino() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearUsuario(7L, GestorListaDeseos.Usuario.Rol.PROPIETARIO);
        crearPropiedad(70L);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        assertFalse(service.eliminarPropiedadDeLista(7L, 70L));
    }

    @Test
    public void obtenerPropiedadesDeLista_usuarioSinLista() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearUsuario(8L, GestorListaDeseos.Usuario.Rol.INQUILINO);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        assertTrue(service.obtenerPropiedadesDeLista(8L).isEmpty());
    }

    @Test
    public void estaEnListaDeDeseos_variosCasos() {
        var dao = new GestorListaDeseos.InMemoryListaDeseosDAO();
        crearUsuario(9L, GestorListaDeseos.Usuario.Rol.INQUILINO);
        crearPropiedad(90L);
        crearPropiedad(91L);

        var service = new GestorListaDeseos.ListaDeseosService(
                dao,
                usuarios::get,
                propiedades::get
        );

        assertFalse(service.estaEnListaDeDeseos(9L, 90L));
        assertFalse(service.estaEnListaDeDeseos(9L, 91L));

        service.agregarPropiedadALista(9L, 90L);

        assertTrue(service.estaEnListaDeDeseos(9L, 90L));
        assertFalse(service.estaEnListaDeDeseos(9L, 91L));
    }
}