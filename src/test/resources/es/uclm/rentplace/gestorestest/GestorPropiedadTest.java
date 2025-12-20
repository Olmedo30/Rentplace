package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.gestores.GestorPropiedad;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GestorPropiedadTest {

    private GestorPropiedad gestorPropiedad;
    private PropiedadDAO propiedadDAO;

    @BeforeEach
    public void setUp() {
        propiedadDAO = mock(PropiedadDAO.class);
        gestorPropiedad = new GestorPropiedad();
        gestorPropiedad.propiedadDAO = propiedadDAO; // Inyección del mock
    }

    @Test
    public void testBuscarPropiedadesActivas() {
        Propiedad propiedad1 = new Propiedad();
        Propiedad propiedad2 = new Propiedad();
        when(propiedadDAO.findByActivoTrue()).thenReturn(Arrays.asList(propiedad1, propiedad2));

        List<Propiedad> propiedadesActivas = gestorPropiedad.buscarPropiedadesActivas();

        assertEquals(2, propiedadesActivas.size());
        verify(propiedadDAO, times(1)).findByActivoTrue();
    }

    @Test
    public void testObtenerTiposInmueblesActivos() {
        when(propiedadDAO.findDistinctTipoInmuebleByActivoTrue()).thenReturn(Arrays.asList("Apartamento", "Casa"));

        List<String> tipos = gestorPropiedad.obtenerTiposInmueblesActivos();

        assertEquals(2, tipos.size());
        assertTrue(tipos.contains("Apartamento"));
        assertTrue(tipos.contains("Casa"));
    }

    @Test
    public void testBuscarPropiedadesAvanzadas() {
        Propiedad propiedad = new Propiedad();
        when(propiedadDAO.buscarAvanzado("Madrid", "Apartamento", BigDecimal.valueOf(1000), true)).thenReturn(List.of(propiedad));

        List<Propiedad> resultados = gestorPropiedad.buscarPropiedadesAvanzadas("Madrid", "Apartamento", BigDecimal.valueOf(1000), true);

        assertEquals(1, resultados.size());
        assertEquals(propiedad, resultados.get(0));
    }

    @Test
    public void testBuscarPropiedadesActivasPorPropietario_ValidId() {
        Propiedad propiedad = new Propiedad();
        when(propiedadDAO.findByPropietarioIdAndActivoTrue(1L)).thenReturn(List.of(propiedad));

        List<Propiedad> propiedades = gestorPropiedad.buscarPropiedadesActivasPorPropietario(1L);

        assertEquals(1, propiedades.size());
        assertEquals(propiedad, propiedades.get(0));
    }

    @Test
    public void testBuscarPropiedadesActivasPorPropietario_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> gestorPropiedad.buscarPropiedadesActivasPorPropietario(0L));
    }

    @Test
    public void testBuscarPorRangoDePrecios_ValidRange() {
        Propiedad propiedad1 = new Propiedad();
        Propiedad propiedad2 = new Propiedad();
        when(propiedadDAO.findByPrecioNocheBetweenAndActivoTrue(BigDecimal.valueOf(100), BigDecimal.valueOf(500)))
                .thenReturn(Arrays.asList(propiedad1, propiedad2));

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