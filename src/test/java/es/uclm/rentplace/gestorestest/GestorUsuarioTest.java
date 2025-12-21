package es.uclm.rentplace.gestorestest;

import org.junit.jupiter.api.Test;

import es.uclm.rentplace.gestores.GestorUsuario;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GestorUsuarioTest {

    @Test
    public void registrarUsuario_exito() {
        var dao = new GestorUsuario.InMemoryUsuarioDAO();
        var encoder = new GestorUsuario.SimplePasswordEncoder();
        var service = new GestorUsuario.UsuarioService(dao, encoder);
        boolean result = service.registrarUsuario("juan", "secret", "juan@x.com", "600", GestorUsuario.Usuario.Rol.PROPIETARIO);
        assertTrue(result);
        var user = dao.findByUsername("juan").orElse(null);
        assertNotNull(user);
        assertEquals("enc:secret", user.getPassword());
        assertEquals(GestorUsuario.Usuario.Rol.PROPIETARIO, user.getRol());
    }

    @Test
    public void registrarUsuario_camposInvalidos_falla() {
        var dao = new GestorUsuario.InMemoryUsuarioDAO();
        var encoder = new GestorUsuario.SimplePasswordEncoder();
        var service = new GestorUsuario.UsuarioService(dao, encoder);
        assertFalse(service.registrarUsuario(null, "p", "e@e", "600", GestorUsuario.Usuario.Rol.PROPIETARIO));
        assertFalse(service.registrarUsuario("u", "", "e@e", "600", GestorUsuario.Usuario.Rol.PROPIETARIO));
    }

    @Test
    public void autenticar_correctoYincorrecto() {
        var dao = new GestorUsuario.InMemoryUsuarioDAO();
        var encoder = new GestorUsuario.SimplePasswordEncoder();
        var servicio = new GestorUsuario.UsuarioService(dao, encoder);
        var u = new GestorUsuario.Usuario(null, "ana", encoder.encode("pwd"), "ana@x.com", "600", GestorUsuario.Usuario.Rol.INQUILINO);
        dao.preload(u);
        assertTrue(servicio.autenticar("ana", "pwd"));
        assertFalse(servicio.autenticar("ana", "wrong"));
        assertFalse(servicio.autenticar("noexist", "pwd"));
    }

    @Test
    public void actualizarPerfil_casos() {
        var dao = new GestorUsuario.InMemoryUsuarioDAO();
        var encoder = new GestorUsuario.SimplePasswordEncoder();
        var servicio = new GestorUsuario.UsuarioService(dao, encoder);
        var u = new GestorUsuario.Usuario(null, "mar", encoder.encode("p"), "mar@x.com", "600", GestorUsuario.Usuario.Rol.PROPIETARIO);
        dao.preload(u);
        assertTrue(servicio.actualizarPerfil(u.getId(), "nuevo@x.com", "700", null, null, null));
        assertEquals("nuevo@x.com", dao.findById(u.getId()).get().getEmail());
        assertFalse(servicio.actualizarPerfil(999L, "a@b.com", "700", null, null, null));
        dao.preload(new GestorUsuario.Usuario(null, "otro", encoder.encode("p"), "used@x.com", "600", GestorUsuario.Usuario.Rol.INQUILINO));
        assertFalse(servicio.actualizarPerfil(u.getId(), "used@x.com", "700", null, null, null));
    }

    @Test
    public void cambiarContrasena_casos() {
        var dao = new GestorUsuario.InMemoryUsuarioDAO();
        var encoder = new GestorUsuario.SimplePasswordEncoder();
        var servicio = new GestorUsuario.UsuarioService(dao, encoder);
        var u = new GestorUsuario.Usuario(null, "luis", encoder.encode("old"), "luis@x.com", "600", GestorUsuario.Usuario.Rol.INQUILINO);
        dao.preload(u);
        assertFalse(servicio.cambiarContrasena(999L, "old", "new"));
        assertFalse(servicio.cambiarContrasena(u.getId(), "wrong", "new"));
        assertTrue(servicio.cambiarContrasena(u.getId(), "old", "new"));
        assertTrue(encoder.matches("new", dao.findById(u.getId()).get().getPassword()));
    }

    @Test
    public void esPropietarioYEsInquilino() {
        var dao = new GestorUsuario.InMemoryUsuarioDAO();
        var encoder = new GestorUsuario.SimplePasswordEncoder();
        var servicio = new GestorUsuario.UsuarioService(dao, encoder);
        var p = new GestorUsuario.Usuario(null, "prop", encoder.encode("p"), "prop@x.com", "600", GestorUsuario.Usuario.Rol.PROPIETARIO);
        var i = new GestorUsuario.Usuario(null, "inq", encoder.encode("p"), "inq@x.com", "600", GestorUsuario.Usuario.Rol.INQUILINO);
        dao.preload(p);
        dao.preload(i);
        assertTrue(servicio.esPropietario(p.getId()));
        assertFalse(servicio.esPropietario(i.getId()));
        assertTrue(servicio.esInquilino(i.getId()));
        assertFalse(servicio.esInquilino(p.getId()));
    }

    @Test
    public void obtenerUsuariosPorRol_devuelveLista() {
        var dao = new GestorUsuario.InMemoryUsuarioDAO();
        var encoder = new GestorUsuario.SimplePasswordEncoder();
        var servicio = new GestorUsuario.UsuarioService(dao, encoder);
        dao.preload(new GestorUsuario.Usuario(null, "a", encoder.encode("p"), "a@x.com", "1", GestorUsuario.Usuario.Rol.PROPIETARIO));
        dao.preload(new GestorUsuario.Usuario(null, "b", encoder.encode("p"), "b@x.com", "2", GestorUsuario.Usuario.Rol.INQUILINO));
        var propietarios = servicio.obtenerUsuariosPorRol(GestorUsuario.Usuario.Rol.PROPIETARIO);
        assertEquals(1, propietarios.size());
    }
}
