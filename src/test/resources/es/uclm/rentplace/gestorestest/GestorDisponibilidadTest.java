package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.entity.Disponibilidad;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.gestores.GestorDisponibilidad;
import es.uclm.rentplace.persistence.DisponibilidadDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GestorDisponibilidadTest {

    private GestorDisponibilidad gestorDisponibilidad;
    private DisponibilidadDAO disponibilidadDAO;

    @BeforeEach
    public void setUp() {
        disponibilidadDAO = mock(DisponibilidadDAO.class);
        gestorDisponibilidad = new GestorDisponibilidad();
        gestorDisponibilidad.disponibilidadDAO = disponibilidadDAO; // Inject mock
    }

    @Test
    public void testBuscarDisponibilidadesPorPropiedad_ValidID() {
        Propiedad propiedad = new Propiedad();
        Disponibilidad disp1 = new Disponibilidad(propiedad, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15), true, BigDecimal.valueOf(100));
        Disponibilidad disp2 = new Disponibilidad(propiedad, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 15), true, BigDecimal.valueOf(200));
        when(disponibilidadDAO.findByPropiedadId(1L)).thenReturn(Arrays.asList(disp1, disp2));

        List<Disponibilidad> result = gestorDisponibilidad.buscarDisponibilidadesPorPropiedad(1L);

        assertEquals(2, result.size());
        assertTrue(result.contains(disp1));
        assertTrue(result.contains(disp2));
    }

    @Test
    public void testBuscarDisponibilidadesPorPropiedad_InvalidID() {
        assertThrows(IllegalArgumentException.class, () -> gestorDisponibilidad.buscarDisponibilidadesPorPropiedad(0L));
    }

    @Test
    public void testBuscarDisponibilidadesEnIntervalo_ValidParams() {
        Propiedad propiedad = new Propiedad();
        Disponibilidad disp = new Disponibilidad(propiedad, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15), true, BigDecimal.valueOf(100));
        when(disponibilidadDAO.findByPropiedadIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                1L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10)))
                .thenReturn(List.of(disp));

        List<Disponibilidad> result = gestorDisponibilidad.buscarDisponibilidadesEnIntervalo(
                1L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10));

        assertEquals(1, result.size());
        assertEquals(disp, result.get(0));
    }

    @Test
    public void testBuscarDisponibilidadesEnIntervalo_InvalidDates() {
        assertThrows(IllegalArgumentException.class, () -> gestorDisponibilidad.buscarDisponibilidadesEnIntervalo(
                1L, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 1)));
    }

    @Test
    public void testCrearDisponibilidad_ValidParams() {
        Propiedad propiedad = new Propiedad();
        Disponibilidad expectedDisponibilidad = new Disponibilidad(propiedad, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15), true, BigDecimal.valueOf(100));
        when(disponibilidadDAO.save(Mockito.any(Disponibilidad.class))).thenReturn(expectedDisponibilidad);

        Disponibilidad result = gestorDisponibilidad.crearDisponibilidad(
                propiedad, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15), true, BigDecimal.valueOf(100));

        assertNotNull(result);
        assertEquals(expectedDisponibilidad, result);
    }

    @Test
    public void testCrearDisponibilidad_InvalidParams() {
        Propiedad propiedad = new Propiedad();
        assertThrows(IllegalArgumentException.class, () -> gestorDisponibilidad.crearDisponibilidad(
                propiedad, LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 1), true, BigDecimal.valueOf(100)));
    }
}
