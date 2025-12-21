package es.uclm.rentplace.gestorestest;

import org.junit.jupiter.api.Test;

import es.uclm.rentplace.gestores.GestorBusqueda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GestorBusquedaTest {

    @Test
    public void buscar_sinFiltros_devuelvePaginado() {
        var props = List.of(
            new GestorBusqueda.Propiedad(1L, "Apartamento", "Madrid", 2, BigDecimal.valueOf(50), true),
            new GestorBusqueda.Propiedad(2L, "Estudio", "Toledo", 1, BigDecimal.valueOf(30), true),
            new GestorBusqueda.Propiedad(3L, "Apartamento", "Madrid Centro", 4, BigDecimal.valueOf(80), true)
        );
        var dao = new GestorBusqueda.InMemoryPropiedadDAO(props);
        var service = new GestorBusqueda.BusquedaService(dao);
        var gestor = new GestorBusqueda(service);
        var criterios = new GestorBusqueda.BusquedaPropiedadDTO();
        var page = gestor.buscar(criterios, 0, 2);
        assertEquals(2, page.getContent().size());
        assertEquals(2, page.getTotalPages());
        assertEquals(3, page.getTotalElements());
    }

    @Test
    public void buscar_filtradoPorCiudad_caseInsensitive() {
        var props = List.of(
            new GestorBusqueda.Propiedad(1L, "Apartamento", "Madrid", 2, BigDecimal.valueOf(50), true),
            new GestorBusqueda.Propiedad(2L, "Estudio", "toledo", 1, BigDecimal.valueOf(30), true),
            new GestorBusqueda.Propiedad(3L, "Apartamento", "MADRID CENTRO", 4, BigDecimal.valueOf(80), true)
        );
        var dao = new GestorBusqueda.InMemoryPropiedadDAO(props);
        var service = new GestorBusqueda.BusquedaService(dao);
        var gestor = new GestorBusqueda(service);
        var criterios = new GestorBusqueda.BusquedaPropiedadDTO();
        criterios.setCiudad("madr");
        var page = gestor.buscar(criterios, 0, 10);
        assertEquals(2, page.getContent().size());
        assertTrue(page.getContent().stream().allMatch(p -> p.getCiudad().toLowerCase().contains("madr")));
    }

    @Test
    public void buscar_filtradoPorTipoExacto() {
        var props = List.of(
            new GestorBusqueda.Propiedad(1L, "Apartamento", "Madrid", 2, BigDecimal.valueOf(50), true),
            new GestorBusqueda.Propiedad(2L, "Estudio", "Toledo", 1, BigDecimal.valueOf(30), true),
            new GestorBusqueda.Propiedad(3L, "Apartamento", "Sevilla", 4, BigDecimal.valueOf(80), true)
        );
        var dao = new GestorBusqueda.InMemoryPropiedadDAO(props);
        var service = new GestorBusqueda.BusquedaService(dao);
        var gestor = new GestorBusqueda(service);
        var criterios = new GestorBusqueda.BusquedaPropiedadDTO();
        criterios.setTipoInmueble("Apartamento");
        var page = gestor.buscar(criterios, 0, 10);
        assertEquals(2, page.getContent().size());
        assertTrue(page.getContent().stream().allMatch(p -> "Apartamento".equals(p.getTipoInmueble())));
    }

    @Test
    public void buscar_precioYCapacidad_filtraCorrectamente() {
        var props = List.of(
            new GestorBusqueda.Propiedad(1L, "Apartamento", "Madrid", 2, BigDecimal.valueOf(50), true),
            new GestorBusqueda.Propiedad(2L, "Apartamento", "Madrid", 4, BigDecimal.valueOf(150), true),
            new GestorBusqueda.Propiedad(3L, "Apartamento", "Madrid", 3, BigDecimal.valueOf(75), true)
        );
        var dao = new GestorBusqueda.InMemoryPropiedadDAO(props);
        var service = new GestorBusqueda.BusquedaService(dao);
        var gestor = new GestorBusqueda(service);
        var criterios = new GestorBusqueda.BusquedaPropiedadDTO();
        criterios.setPrecioMaximo(BigDecimal.valueOf(100));
        criterios.setCapacidadMinima(3);
        var page = gestor.buscar(criterios, 0, 10);
        assertEquals(1, page.getContent().size());
        var p = page.getContent().get(0);
        assertTrue(p.getCapacidad() >= 3);
        assertTrue(p.getPrecioNoche().compareTo(BigDecimal.valueOf(100)) <= 0);
    }

    @Test
    public void buscar_paginaFueraDeRango_devuelveVacio() {
        var props = List.of(
            new GestorBusqueda.Propiedad(1L, "Apartamento", "Madrid", 2, BigDecimal.valueOf(50), true)
        );
        var dao = new GestorBusqueda.InMemoryPropiedadDAO(props);
        var service = new GestorBusqueda.BusquedaService(dao);
        var gestor = new GestorBusqueda(service);
        var criterios = new GestorBusqueda.BusquedaPropiedadDTO();
        var page = gestor.buscar(criterios, 10, 5);
        assertEquals(0, page.getContent().size());
        assertEquals(1, page.getTotalElements());
    }
}