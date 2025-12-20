package es.uclm.rentplace.gestorestest;

import org.junit.jupiter.api.Test;

import es.uclm.rentplace.gestores.GestorRol;

import static org.junit.jupiter.api.Assertions.*;

public class GestorRolTest {

    @Test
    public void convertir_exito_actualizaRolYSession() {
        var dao = new GestorRol.InMemoryUsuarioDAO();
        var usuario = new GestorRol.Usuario(1L, "juan", GestorRol.Usuario.Rol.PROPIETARIO);
        dao.add(usuario);
        var gestor = new GestorRol(dao);
        var session = new GestorRol.SimpleSession();
        session.setAttribute("userId", 1L);
        var model = new GestorRol.SimpleModel();
        var result = gestor.convertirAInquilino(session, model);
        assertEquals("redirect:/profile", result);
        assertEquals("INQUILINO", session.getAttribute("rol"));
        assertEquals(GestorRol.Usuario.Rol.INQUILINO, dao.findById(1L).get().getRol());
        assertNotNull(model.get("message"));
    }

    @Test
    public void convertir_sinSesion_devuelveLoginConError() {
        var dao = new GestorRol.InMemoryUsuarioDAO();
        var gestor = new GestorRol(dao);
        var session = new GestorRol.SimpleSession();
        var model = new GestorRol.SimpleModel();
        var result = gestor.convertirAInquilino(session, model);
        assertEquals("login", result);
        assertEquals("Debes iniciar sesión.", model.get("error"));
    }

    @Test
    public void convertir_usuarioNoEncontrado_devuelveHomeConError() {
        var dao = new GestorRol.InMemoryUsuarioDAO();
        var gestor = new GestorRol(dao);
        var session = new GestorRol.SimpleSession();
        session.setAttribute("userId", 99L);
        var model = new GestorRol.SimpleModel();
        var result = gestor.convertirAInquilino(session, model);
        assertEquals("home", result);
        assertEquals("Usuario no encontrado.", model.get("error"));
    }

    @Test
    public void convertir_atributoNoLong_lanzaClassCastException() {
        var dao = new GestorRol.InMemoryUsuarioDAO();
        var gestor = new GestorRol(dao);
        var session = new GestorRol.SimpleSession();
        session.setAttribute("userId", "42");
        var model = new GestorRol.SimpleModel();
        assertThrows(ClassCastException.class, () -> gestor.convertirAInquilino(session, model));
    }

    @Test
    public void convertir_sessionNull_lanzaNullPointerException() {
        var dao = new GestorRol.InMemoryUsuarioDAO();
        var gestor = new GestorRol(dao);
        GestorRol.Session session = null;
        var model = new GestorRol.SimpleModel();
        assertThrows(NullPointerException.class, () -> gestor.convertirAInquilino(session, model));
    }
}