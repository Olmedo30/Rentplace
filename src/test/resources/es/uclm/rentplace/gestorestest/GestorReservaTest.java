package es.uclm.rentplace.gestorestest;

import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.gestores.GestorReserva;
import es.uclm.rentplace.persistence.ReservaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GestorReservaTest {

    private GestorReserva gestorReserva;
    private TestReservaDAO reservaDAO;

    @BeforeEach
    public void setUp() {
        reservaDAO = new TestReservaDAO();
        gestorReserva = new GestorReserva();
        gestorReserva.reservaDAO = reservaDAO;
    }
    
    private static class TestReservaDAO implements ReservaDAO {
        private Map<Long, Reserva> reservas = new HashMap<>();
        private Long idCounter = 1L;
        
        @Override
        public List<Reserva> findSolapadas(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
            return reservas.values().stream()
                .filter(r -> r.getPropiedad().getId().equals(propiedadId))
                .filter(r -> r.getReservaConfirmada() != null && r.getReservaConfirmada())
                .filter(r -> (r.getFechaEntrada().isBefore(fechaSalida) || r.getFechaEntrada().isEqual(fechaSalida)) 
                         && (r.getFechaSalida().isAfter(fechaEntrada) || r.getFechaSalida().isEqual(fechaEntrada)))
                .collect(Collectors.toList());
        }
        
        @Override
        public List<Reserva> findByInquilinoId(Long inquilinoId) {
            return reservas.values().stream()
                .filter(r -> r.getInquilino().getId().equals(inquilinoId))
                .collect(Collectors.toList());
        }
        
        @Override
        public List<Reserva> findReservasConfirmadasEnRango(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
            return reservas.values().stream()
                .filter(r -> r.getPropiedad().getId().equals(propiedadId))
                .filter(r -> r.getReservaConfirmada() != null && r.getReservaConfirmada())
                .filter(r -> (r.getFechaEntrada().isBefore(fechaSalida) || r.getFechaEntrada().isEqual(fechaSalida)) 
                         && (r.getFechaSalida().isAfter(fechaEntrada) || r.getFechaSalida().isEqual(fechaEntrada)))
                .collect(Collectors.toList());
        }
        
        @Override
        public List<Reserva> findTodasReservasEnRango(Long propiedadId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
            return reservas.values().stream()
                .filter(r -> r.getPropiedad().getId().equals(propiedadId))
                .filter(r -> (r.getFechaEntrada().isBefore(fechaSalida) || r.getFechaEntrada().isEqual(fechaSalida)) 
                         && (r.getFechaSalida().isAfter(fechaEntrada) || r.getFechaSalida().isEqual(fechaEntrada)))
                .collect(Collectors.toList());
        }
        
        @Override
        public <S extends Reserva> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idCounter++);
            }
            reservas.put(entity.getId(), entity);
            return entity;
        }
        
        public void addReserva(Reserva r) {
            save(r);
        }
        
        @Override public Optional<Reserva> findById(Long id) { return Optional.ofNullable(reservas.get(id)); }
        @Override public List<Reserva> findAll() { return new ArrayList<>(reservas.values()); }
        @Override public List<Reserva> findAllById(Iterable<Long> ids) { return null; }
        @Override public long count() { return reservas.size(); }
        @Override public void deleteById(Long id) { reservas.remove(id); }
        @Override public void delete(Reserva entity) { reservas.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends Reserva> entities) {}
        @Override public void deleteAll() { reservas.clear(); }
        @Override public <S extends Reserva> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public boolean existsById(Long id) { return reservas.containsKey(id); }
        @Override public void flush() {}
        @Override public <S extends Reserva> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Reserva> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<Reserva> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override public Reserva getOne(Long id) { return null; }
        @Override public Reserva getById(Long id) { return reservas.get(id); }
        @Override public Reserva getReferenceById(Long id) { return reservas.get(id); }
        @Override public <S extends Reserva> Optional<S> findOne(org.springframework.data.domain.Example<S> example) { return Optional.empty(); }
        @Override public <S extends Reserva> List<S> findAll(org.springframework.data.domain.Example<S> example) { return null; }
        @Override public <S extends Reserva> List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) { return null; }
        @Override public <S extends Reserva> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) { return null; }
        @Override public <S extends Reserva> long count(org.springframework.data.domain.Example<S> example) { return 0; }
        @Override public <S extends Reserva> boolean exists(org.springframework.data.domain.Example<S> example) { return false; }
        @Override public <S extends Reserva, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<Reserva> findAll(org.springframework.data.domain.Sort sort) { return null; }
        @Override public org.springframework.data.domain.Page<Reserva> findAll(org.springframework.data.domain.Pageable pageable) { return null; }
    }

    @Test
    public void testBuscarReservasSolapadas_ValidParams() {
        es.uclm.rentplace.entity.Propiedad propiedad = new es.uclm.rentplace.entity.Propiedad();
        propiedad.setId(1L);
        es.uclm.rentplace.entity.Usuario inquilino = new es.uclm.rentplace.entity.Usuario();
        inquilino.setId(1L);
        
        Reserva reserva1 = new Reserva();
        reserva1.setPropiedad(propiedad);
        reserva1.setInquilino(inquilino);
        reserva1.setFechaEntrada(LocalDateTime.of(2025, 1, 1, 12, 0));
        reserva1.setFechaSalida(LocalDateTime.of(2025, 1, 5, 12, 0));
        reserva1.setReservaConfirmada(true);
        reservaDAO.addReserva(reserva1);
        
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);

        List<Reserva> resultado = gestorReserva.buscarReservasSolapadas(1L, entrada, salida);

        assertEquals(1, resultado.size());
    }

    @Test
    public void testBuscarReservasSolapadas_PropiedadIdInvalido() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.buscarReservasSolapadas(0L, entrada, salida));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.buscarReservasSolapadas(-1L, entrada, salida));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.buscarReservasSolapadas(null, entrada, salida));
    }

    @Test
    public void testBuscarReservasSolapadas_FechasNulas() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.buscarReservasSolapadas(1L, null, salida));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.buscarReservasSolapadas(1L, entrada, null));
    }

    @Test
    public void testBuscarReservasSolapadas_FechasInvertidas() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 5, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 1, 12, 0);
        
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.buscarReservasSolapadas(1L, entrada, salida));
    }

    @Test
    public void testBuscarReservasPorInquilino_ValidId() {
        es.uclm.rentplace.entity.Usuario inquilino = new es.uclm.rentplace.entity.Usuario();
        inquilino.setId(1L);
        es.uclm.rentplace.entity.Propiedad propiedad = new es.uclm.rentplace.entity.Propiedad();
        propiedad.setId(1L);
        
        Reserva reserva = new Reserva();
        reserva.setInquilino(inquilino);
        reserva.setPropiedad(propiedad);
        reservaDAO.addReserva(reserva);

        List<Reserva> resultado = gestorReserva.buscarReservasPorInquilino(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    public void testBuscarReservasPorInquilino_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.buscarReservasPorInquilino(0L));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.buscarReservasPorInquilino(-1L));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.buscarReservasPorInquilino(null));
    }

    @Test
    public void testVerificarDisponibilidadInmediata_Disponible() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);

        boolean disponible = gestorReserva.verificarDisponibilidadInmediata(1L, entrada, salida);

        assertTrue(disponible);
    }

    @Test
    public void testVerificarDisponibilidadInmediata_NoDisponible() {
        es.uclm.rentplace.entity.Propiedad propiedad = new es.uclm.rentplace.entity.Propiedad();
        propiedad.setId(1L);
        es.uclm.rentplace.entity.Usuario inquilino = new es.uclm.rentplace.entity.Usuario();
        inquilino.setId(1L);
        
        Reserva reserva = new Reserva();
        reserva.setPropiedad(propiedad);
        reserva.setInquilino(inquilino);
        reserva.setFechaEntrada(LocalDateTime.of(2025, 1, 1, 12, 0));
        reserva.setFechaSalida(LocalDateTime.of(2025, 1, 5, 12, 0));
        reserva.setReservaConfirmada(true);
        reservaDAO.addReserva(reserva);
        
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);

        boolean disponible = gestorReserva.verificarDisponibilidadInmediata(1L, entrada, salida);

        assertFalse(disponible);
    }

    @Test
    public void testVerificarDisponibilidadInmediata_ParamsInvalidos() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.verificarDisponibilidadInmediata(0L, entrada, salida));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.verificarDisponibilidadInmediata(1L, null, salida));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.verificarDisponibilidadInmediata(1L, entrada, null));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.verificarDisponibilidadInmediata(1L, salida, entrada));
    }

    @Test
    public void testVerificarDisponibilidadNoInmediata_Disponible() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);

        boolean disponible = gestorReserva.verificarDisponibilidadNoInmediata(1L, entrada, salida);

        assertTrue(disponible);
    }

    @Test
    public void testVerificarDisponibilidadNoInmediata_NoDisponible() {
        es.uclm.rentplace.entity.Propiedad propiedad = new es.uclm.rentplace.entity.Propiedad();
        propiedad.setId(1L);
        es.uclm.rentplace.entity.Usuario inquilino = new es.uclm.rentplace.entity.Usuario();
        inquilino.setId(1L);
        
        Reserva reserva = new Reserva();
        reserva.setPropiedad(propiedad);
        reserva.setInquilino(inquilino);
        reserva.setFechaEntrada(LocalDateTime.of(2025, 1, 1, 12, 0));
        reserva.setFechaSalida(LocalDateTime.of(2025, 1, 5, 12, 0));
        reservaDAO.addReserva(reserva);
        
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);

        boolean disponible = gestorReserva.verificarDisponibilidadNoInmediata(1L, entrada, salida);

        assertFalse(disponible);
    }

    @Test
    public void testVerificarDisponibilidadNoInmediata_ParamsInvalidos() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.verificarDisponibilidadNoInmediata(null, entrada, salida));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.verificarDisponibilidadNoInmediata(1L, null, salida));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.verificarDisponibilidadNoInmediata(1L, entrada, null));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.verificarDisponibilidadNoInmediata(1L, salida, entrada));
    }

    @Test
    public void testCalcularImporteTotal_ValidParams() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 10, 12, 0);
        BigDecimal precioNoche = BigDecimal.valueOf(100);

        BigDecimal total = gestorReserva.calcularImporteTotal(entrada, salida, precioNoche);

        assertEquals(BigDecimal.valueOf(900), total);
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

        assertEquals(BigDecimal.valueOf(100), total);
    }

    @Test
    public void testCalcularImporteTotal_FechasNulas() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        BigDecimal precio = BigDecimal.valueOf(100);
        
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.calcularImporteTotal(null, entrada, precio));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.calcularImporteTotal(entrada, null, precio));
    }

    @Test
    public void testCalcularImporteTotal_PrecioInvalido() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 5, 12, 0);
        
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.calcularImporteTotal(entrada, salida, null));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.calcularImporteTotal(entrada, salida, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.calcularImporteTotal(entrada, salida, BigDecimal.valueOf(-100)));
    }

    @Test
    public void testCalcularImporteTotal_FechasInvertidas() {
        LocalDateTime entrada = LocalDateTime.of(2025, 1, 5, 12, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 1, 1, 12, 0);
        BigDecimal precio = BigDecimal.valueOf(100);
        
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.calcularImporteTotal(entrada, salida, precio));
    }

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
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.estaReservaActiva(null));
    }

    @Test
    public void testEstaReservaActiva_FechaSalidaNula() {
        Reserva reserva = new Reserva();
        reserva.setFechaSalida(null);

        assertThrows(IllegalArgumentException.class, () -> gestorReserva.estaReservaActiva(reserva));
    }

    @Test
    public void testObtenerReservaPorId_ReservaExiste() {
        Reserva reserva = new Reserva();
        reservaDAO.addReserva(reserva);
        Long id = reserva.getId();

        Reserva resultado = gestorReserva.obtenerReservaPorId(id);

        assertNotNull(resultado);
    }

    @Test
    public void testObtenerReservaPorId_ReservaNoExiste() {
        Reserva resultado = gestorReserva.obtenerReservaPorId(999L);

        assertNull(resultado);
    }

    @Test
    public void testObtenerReservaPorId_IdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.obtenerReservaPorId(0L));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.obtenerReservaPorId(-1L));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.obtenerReservaPorId(null));
    }

    @Test
    public void testConfirmarReserva_ReservaExiste() {
        Reserva reserva = new Reserva();
        reserva.setReservaConfirmada(false);
        reservaDAO.addReserva(reserva);
        Long id = reserva.getId();

        gestorReserva.confirmarReserva(id);

        assertTrue(reserva.getReservaConfirmada());
    }

    @Test
    public void testConfirmarReserva_ReservaNoExiste() {
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.confirmarReserva(999L));
    }

    @Test
    public void testConfirmarReserva_IdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.confirmarReserva(0L));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.confirmarReserva(-1L));
        assertThrows(IllegalArgumentException.class, () -> gestorReserva.confirmarReserva(null));
    }
}
