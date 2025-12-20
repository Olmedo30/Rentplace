package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.gestores.GestorReserva;
import es.uclm.rentplace.persistence.ReservaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GestorReservaTest {

    private GestorReserva gestorReserva;
    private ReservaDAO reservaDAO;

    @BeforeEach
    public void setUp() {
        reservaDAO = mock(ReservaDAO.class);
        gestorReserva = new GestorReserva();
        gestorReserva.reservaDAO = reservaDAO;
    }

    // TESTS para buscarReservasSolapadas 

    @Test
    public void testBuscarReservasSolapadas_ValidParams() {
        Reserva reserva1 = new Reserva();
        Reserva reserva2 = new Reserva();
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        when(reservaDAO.findSolapadas(1L, entrada, salida))
            .thenReturn(Arrays.asList(reserva1, reserva2));

        List<Reserva> resultado = gestorReserva.buscarReservasSolapadas(1L, entrada, salida);

        assertEquals(2, resultado.size());
        verify(reservaDAO, times(1)).findSolapadas(1L, entrada, salida);
    }

    @Test
    public void testBuscarReservasSolapadas_PropiedadIdInvalido() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.buscarReservasSolapadas(0L, entrada, salida));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.buscarReservasSolapadas(-1L, entrada, salida));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.buscarReservasSolapadas(null, entrada, salida));
    }

    @Test
    public void testBuscarReservasSolapadas_FechasNulas() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.buscarReservasSolapadas(1L, null, salida));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.buscarReservasSolapadas(1L, entrada, null));
    }

    @Test
    public void testBuscarReservasSolapadas_FechasInvertidas() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 5, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 1, 12, 0);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.buscarReservasSolapadas(1L, entrada, salida));
    }

    // TESTS para buscarReservasPorInquilino 

    @Test
    public void testBuscarReservasPorInquilino_ValidId() {
        Reserva reserva = new Reserva();
        when(reservaDAO.findByInquilinoId(1L)).thenReturn(List.of(reserva));

        List<Reserva> resultado = gestorReserva.buscarReservasPorInquilino(1L);

        assertEquals(1, resultado.size());
        assertEquals(reserva, resultado.get(0));
    }

    @Test
    public void testBuscarReservasPorInquilino_InvalidId() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.buscarReservasPorInquilino(0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.buscarReservasPorInquilino(-1L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.buscarReservasPorInquilino(null));
    }

    // TESTS para verificarDisponibilidadInmediata 

    @Test
    public void testVerificarDisponibilidadInmediata_Disponible() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        when(reservaDAO.findReservasConfirmadasEnRango(1L, entrada, salida))
            .thenReturn(List.of());

        boolean disponible = gestorReserva.verificarDisponibilidadInmediata(1L, entrada, salida);

        assertTrue(disponible);
    }

    @Test
    public void testVerificarDisponibilidadInmediata_NoDisponible() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        Reserva reserva = new Reserva();
        
        when(reservaDAO.findReservasConfirmadasEnRango(1L, entrada, salida))
            .thenReturn(List.of(reserva));

        boolean disponible = gestorReserva.verificarDisponibilidadInmediata(1L, entrada, salida);

        assertFalse(disponible);
    }

    @Test
    public void testVerificarDisponibilidadInmediata_ParamsInvalidos() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.verificarDisponibilidadInmediata(0L, entrada, salida));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.verificarDisponibilidadInmediata(1L, null, salida));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.verificarDisponibilidadInmediata(1L, entrada, null));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.verificarDisponibilidadInmediata(1L, salida, entrada));
    }

    // TESTS para verificarDisponibilidadNoInmediata 

    @Test
    public void testVerificarDisponibilidadNoInmediata_Disponible() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        when(reservaDAO.findTodasReservasEnRango(1L, entrada, salida))
            .thenReturn(List.of());

        boolean disponible = gestorReserva.verificarDisponibilidadNoInmediata(1L, entrada, salida);

        assertTrue(disponible);
    }

    @Test
    public void testVerificarDisponibilidadNoInmediata_NoDisponible() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        Reserva reserva = new Reserva();
        
        when(reservaDAO.findTodasReservasEnRango(1L, entrada, salida))
            .thenReturn(List.of(reserva));

        boolean disponible = gestorReserva.verificarDisponibilidadNoInmediata(1L, entrada, salida);

        assertFalse(disponible);
    }

    @Test
    public void testVerificarDisponibilidadNoInmediata_ParamsInvalidos() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.verificarDisponibilidadNoInmediata(null, entrada, salida));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.verificarDisponibilidadNoInmediata(1L, null, salida));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.verificarDisponibilidadNoInmediata(1L, entrada, null));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.verificarDisponibilidadNoInmediata(1L, salida, entrada));
    }

    // TESTS para calcularImporteTotal 

    @Test
    public void testCalcularImporteTotal_ValidParams() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 10, 12, 0);
        BigDecimal precioNoche = BigDecimal.valueOf(100);

        BigDecimal total = gestorReserva.calcularImporteTotal(entrada, salida, precioNoche);

        assertEquals(BigDecimal.valueOf(900), total); // 9 días × 100
    }

    @Test
    public void testCalcularImporteTotal_UnDia() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 2, 12, 0);
        BigDecimal precioNoche = BigDecimal.valueOf(50);

        BigDecimal total = gestorReserva.calcularImporteTotal(entrada, salida, precioNoche);

        assertEquals(BigDecimal.valueOf(50), total);
    }

    @Test
    public void testCalcularImporteTotal_MismosDia() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 1, 18, 0);
        BigDecimal precioNoche = BigDecimal.valueOf(100);

        BigDecimal total = gestorReserva.calcularImporteTotal(entrada, salida, precioNoche);

        assertEquals(BigDecimal.valueOf(100), total); // Mínimo 1 día
    }

    @Test
    public void testCalcularImporteTotal_FechasNulas() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        BigDecimal precio = BigDecimal.valueOf(100);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.calcularImporteTotal(null, entrada, precio));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.calcularImporteTotal(entrada, null, precio));
    }

    @Test
    public void testCalcularImporteTotal_PrecioInvalido() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.calcularImporteTotal(entrada, salida, null));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.calcularImporteTotal(entrada, salida, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.calcularImporteTotal(entrada, salida, BigDecimal.valueOf(-100)));
    }

    @Test
    public void testCalcularImporteTotal_FechasInvertidas() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 5, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 1, 12, 0);
        BigDecimal precio = BigDecimal.valueOf(100);
        
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.calcularImporteTotal(entrada, salida, precio));
    }

    // TESTS para validarFechasReserva 

    @Test
    public void testValidarFechasReserva_FechasValidas() {
        LocalDateTime entrada = LocalDateTime.now().plusDays(1);
        LocalDateTime salida = LocalDateTime.now().plusDays(5);

        boolean valido = gestorReserva.validarFechasReserva(entrada, salida);

        assertTrue(valido);
    }

    @Test
    public void testValidarFechasReserva_FechasNulas() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        
        assertFalse(gestorReserva.validarFechasReserva(null, fecha));
        assertFalse(gestorReserva.validarFechasReserva(fecha, null));
    }

    @Test
    public void testValidarFechasReserva_EntradaPasada() {
        LocalDateTime entrada = LocalDateTime.now().minusDays(1);
        LocalDateTime salida = LocalDateTime.now().plusDays(5);

        boolean valido = gestorReserva.validarFechasReserva(entrada, salida);

        assertFalse(valido);
    }

    @Test
    public void testValidarFechasReserva_FechasInvertidas() {
        LocalDateTime entrada = LocalDateTime.now().plusDays(5);
        LocalDateTime salida = LocalDateTime.now().plusDays(1);

        boolean valido = gestorReserva.validarFechasReserva(entrada, salida);

        assertFalse(valido);
    }

    @Test
    public void testValidarFechasReserva_FechasIguales() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);

        boolean valido = gestorReserva.validarFechasReserva(fecha, fecha);

        assertFalse(valido);
    }

    // TESTS para estaReservaActiva 

    @Test
    public void testEstaReservaActiva_ReservaActiva() {
        Reserva reserva = new Reserva();
        reserva.setFechaSalida(LocalDateTime.now().plusDays(5));

        boolean activa = gestorReserva.estaReservaActiva(reserva);

        assertTrue(activa);
    }

    @Test
    public void testEstaReservaActiva_ReservaFinalizada() {
        Reserva reserva = new Reserva();
        reserva.setFechaSalida(LocalDateTime.now().minusDays(5));

        boolean activa = gestorReserva.estaReservaActiva(reserva);

        assertFalse(activa);
    }

    @Test
    public void testEstaReservaActiva_ReservaNula() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.estaReservaActiva(null));
    }

    @Test
    public void testEstaReservaActiva_FechaSalidaNula() {
        Reserva reserva = new Reserva();
        reserva.setFechaSalida(null);

        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.estaReservaActiva(reserva));
    }

    // TESTS para obtenerReservaPorId 

    @Test
    public void testObtenerReservaPorId_ReservaExiste() {
        Reserva reserva = new Reserva();
        when(reservaDAO.findById(1L)).thenReturn(Optional.of(reserva));

        Reserva resultado = gestorReserva.obtenerReservaPorId(1L);

        assertNotNull(resultado);
        assertEquals(reserva, resultado);
    }

    @Test
    public void testObtenerReservaPorId_ReservaNoExiste() {
        when(reservaDAO.findById(999L)).thenReturn(Optional.empty());

        Reserva resultado = gestorReserva.obtenerReservaPorId(999L);

        assertNull(resultado);
    }

    @Test
    public void testObtenerReservaPorId_IdInvalido() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.obtenerReservaPorId(0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.obtenerReservaPorId(-1L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.obtenerReservaPorId(null));
    }

    // TESTS para confirmarReserva 

    @Test
    public void testConfirmarReserva_ReservaExiste() {
        Reserva reserva = new Reserva();
        reserva.setReservaConfirmada(false);
        when(reservaDAO.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaDAO.save(any(Reserva.class))).thenReturn(reserva);

        gestorReserva.confirmarReserva(1L);

        assertTrue(reserva.getReservaConfirmada());
        verify(reservaDAO, times(1)).save(reserva);
    }

    @Test
    public void testConfirmarReserva_ReservaNoExiste() {
        when(reservaDAO.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.confirmarReserva(999L));
    }

    @Test
    public void testConfirmarReserva_IdInvalido() {
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.confirmarReserva(0L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.confirmarReserva(-1L));
        assertThrows(IllegalArgumentException.class, 
            () -> gestorReserva.confirmarReserva(null));
    }
}

