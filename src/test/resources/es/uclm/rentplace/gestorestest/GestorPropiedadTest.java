package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.gestores.GestorPropiedad;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GestorPropiedadTest {

    @Test
    public void registrarPropiedad_exito() {
        var dao = new GestorPropiedad.InMemoryPropiedadDAO();
        var service = new GestorPropiedad.PropiedadService(dao);
        var propietario = new GestorPropiedad.Usuario(1L);
        var propiedad = service.registrarPropiedad(propietario,
                "Piso céntrico", "Bonito piso", "Calle Mayor 1", "Madrid",
                "Piso", 2, 4, new BigDecimal("80.50"), "Flexible", true);
        assertNotNull(propiedad);
        assertEquals("Piso céntrico", propiedad.getTitulo());
        assertEquals(propietario.getId(), propiedad.getPropietario().getId());
        assertTrue(propiedad.getActivo());
        assertNotNull(propiedad.getFechaAlta());
    }

    @Test
    public void registrarPropiedad_camposInvalidos_falla() {
        var dao = new GestorPropiedad.InMemoryPropiedadDAO();
        var service = new GestorPropiedad.PropiedadService(dao);
        var propietario = new GestorPropiedad.Usuario(1L);
        assertNull(service.registrarPropiedad(null, "T", "desc", "dir", "ciudad", "tipo", 1, 1, BigDecimal.TEN, "pol", true));
        assertNull(service.registrarPropiedad(propietario, "", "desc", "dir", "ciudad", "tipo", 1, 1, BigDecimal.TEN, "pol", true));
        assertNull(service.registrarPropiedad(propietario, "T", "desc", "", "ciudad", "tipo", 1, 1, BigDecimal.TEN, "pol", true));
        assertNull(service.registrarPropiedad(propietario, "T", "desc", "dir", "", "tipo", 1, 1, BigDecimal.TEN, "pol", true));
        assertNull(service.registrarPropiedad(propietario, "T", "desc", "dir", "ciudad", "", 1, 1, BigDecimal.TEN, "pol", true));
        assertNull(service.registrarPropiedad(propietario, "T", "desc", "dir", "ciudad", "tipo", 0, 1, BigDecimal.TEN, "pol", true));
        assertNull(service.registrarPropiedad(propietario, "T", "desc", "dir", "ciudad", "tipo", 1, -1, BigDecimal.TEN, "pol", true));
        assertNull(service.registrarPropiedad(propietario, "T", "desc", "dir", "ciudad", "tipo", 1, 1, BigDecimal.ZERO, "pol", true));
        assertNull(service.registrarPropiedad(propietario, "T", "desc", "dir", "ciudad", "tipo", 1, 1, new BigDecimal("-5"), "pol", true));
        assertNull(service.registrarPropiedad(propietario, "T", "desc", "dir", "ciudad", "tipo", 1, 1, BigDecimal.TEN, "", true));
    }

    @Test
    public void obtenerPropiedadPorId() {
        var dao = new GestorPropiedad.InMemoryPropiedadDAO();
        var service = new GestorPropiedad.PropiedadService(dao);
        var propietario = new GestorPropiedad.Usuario(1L);
        var p = service.registrarPropiedad(propietario,
                "Casa rural", "Acogedora", "Camino 5", "Toledo", "Casa", 3, 6, new BigDecimal("120"), "Estricta", false);
        assertNotNull(p);
        var recuperada = service.obtenerPropiedadPorId(p.getId());
        assertNotNull(recuperada);
        assertEquals(p.getId(), recuperada.getId());
        assertNull(service.obtenerPropiedadPorId(999L));
    }

    @Test
    public void obtenerPropiedadesDePropietario() {
        var dao = new GestorPropiedad.InMemoryPropiedadDAO();
        var service = new GestorPropiedad.PropiedadService(dao);
        var prop1 = new GestorPropiedad.Usuario(1L);
        var prop2 = new GestorPropiedad.Usuario(2L);

        service.registrarPropiedad(prop1, "A", "desc", "dir", "ciudad", "tipo", 1, 2, BigDecimal.TEN, "pol", true);
        service.registrarPropiedad(prop1, "B", "desc", "dir", "ciudad", "tipo", 1, 2, BigDecimal.TEN, "pol", true);
        service.registrarPropiedad(prop2, "C", "desc", "dir", "ciudad", "tipo", 1, 2, BigDecimal.TEN, "pol", true);

        var de1 = service.obtenerPropiedadesDePropietario(1L);
        var de2 = service.obtenerPropiedadesDePropietario(2L);
        var de3 = service.obtenerPropiedadesDePropietario(3L);

        assertEquals(2, de1.size());
        assertEquals(1, de2.size());
        assertTrue(de3.isEmpty());
    }

    @Test
    public void listarPropiedadesActivas() {
        var dao = new GestorPropiedad.InMemoryPropiedadDAO();
        var service = new GestorPropiedad.PropiedadService(dao);
        var propietario = new GestorPropiedad.Usuario(1L);
        service.registrarPropiedad(propietario, "A", "d", "dir", "ciudad", "tipo", 1, 2, BigDecimal.TEN, "pol", true);
        var todas = service.listarPropiedadesActivas();
        assertEquals(1, todas.size());
    }

    @Test
    public void actualizarPropiedad_exitoYFallo() {
        var dao = new GestorPropiedad.InMemoryPropiedadDAO();
        var service = new GestorPropiedad.PropiedadService(dao);
        var propietario = new GestorPropiedad.Usuario(1L);
        var p = service.registrarPropiedad(propietario,
                "Viejo", "desc", "dir", "ciudad", "tipo", 1, 2, BigDecimal.TEN, "pol", true);
        assertNotNull(p);

        assertTrue(service.actualizarPropiedad(p.getId(),
                "Nuevo", "nueva desc", "nueva dir", "nueva ciudad", "casa", 3, 5,
                new BigDecimal("150.99"), "nueva pol", false));

        var actualizada = service.obtenerPropiedadPorId(p.getId());
        assertEquals("Nuevo", actualizada.getTitulo());
        assertEquals(3, actualizada.getHabitaciones());
        assertEquals(new BigDecimal("150.99"), actualizada.getPrecioNoche());
        assertFalse(actualizada.getPermiteReservaInmediata());

        // Actualizar con datos inválidos → debe fallar
        assertFalse(service.actualizarPropiedad(p.getId(),
                "", "desc", "dir", "ciudad", "tipo", 1, 2, BigDecimal.TEN, "pol", true));

        // ID inexistente
        assertFalse(service.actualizarPropiedad(999L,
                "X", "desc", "dir", "ciudad", "tipo", 1, 2, BigDecimal.TEN, "pol", true));
    }
}