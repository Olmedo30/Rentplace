package es.uclm.rentplace.gestores;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GestorPropiedad {

    // Clase Usuario mínima (como la usa Propiedad)
    public static class Usuario {
        private final Long id;

        public Usuario(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }

    // Entidad Propiedad
    public static class Propiedad {
        private Long id;
        private Usuario propietario;
        private String titulo;
        private String descripcion;
        private String direccion;
        private String ciudad;
        private String tipoInmueble;
        private Integer habitaciones;
        private Integer capacidad;
        private BigDecimal precioNoche;
        private String politicaCancelacion;
        private Boolean permiteReservaInmediata;
        private LocalDateTime fechaAlta;
        private Boolean activo;

        // Constructor vacío necesario (aunque no obligatorio en Java puro, sí para coherencia)
        public Propiedad() {}

        // Getters y Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Usuario getPropietario() { return propietario; }
        public void setPropietario(Usuario propietario) { this.propietario = propietario; }

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }

        public String getCiudad() { return ciudad; }
        public void setCiudad(String ciudad) { this.ciudad = ciudad; }

        public String getTipoInmueble() { return tipoInmueble; }
        public void setTipoInmueble(String tipoInmueble) { this.tipoInmueble = tipoInmueble; }

        public Integer getHabitaciones() { return habitaciones; }
        public void setHabitaciones(Integer habitaciones) { this.habitaciones = habitaciones; }

        public Integer getCapacidad() { return capacidad; }
        public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

        public BigDecimal getPrecioNoche() { return precioNoche; }
        public void setPrecioNoche(BigDecimal precioNoche) { this.precioNoche = precioNoche; }

        public String getPoliticaCancelacion() { return politicaCancelacion; }
        public void setPoliticaCancelacion(String politicaCancelacion) { this.politicaCancelacion = politicaCancelacion; }

        public Boolean getPermiteReservaInmediata() { return permiteReservaInmediata; }
        public void setPermiteReservaInmediata(Boolean permiteReservaInmediata) { this.permiteReservaInmediata = permiteReservaInmediata; }

        public LocalDateTime getFechaAlta() { return fechaAlta; }
        public void setFechaAlta(LocalDateTime fechaAlta) { this.fechaAlta = fechaAlta; }

        public Boolean getActivo() { return activo; }
        public void setActivo(Boolean activo) { this.activo = activo; }
    }

    // DAO en memoria
    public interface PropiedadDAO {
        Propiedad save(Propiedad propiedad);
        Optional<Propiedad> findById(Long id);
        List<Propiedad> findByPropietarioId(Long propietarioId);
        List<Propiedad> findByActivoTrue();
    }

    public static class InMemoryPropiedadDAO implements PropiedadDAO {
        private final ConcurrentHashMap<Long, Propiedad> storage = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(1);

        @Override
        public Propiedad save(Propiedad propiedad) {
            if (propiedad.getId() == null) {
                propiedad.setId(idGenerator.getAndIncrement());
            }
            if (propiedad.getFechaAlta() == null) {
                propiedad.setFechaAlta(LocalDateTime.now());
            }
            if (propiedad.getActivo() == null) {
                propiedad.setActivo(true);
            }
            storage.put(propiedad.getId(), propiedad);
            return propiedad;
        }

        @Override
        public Optional<Propiedad> findById(Long id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Propiedad> findByPropietarioId(Long propietarioId) {
            return storage.values().stream()
                    .filter(p -> p.getPropietario().getId().equals(propietarioId))
                    .toList();
        }

        @Override
        public List<Propiedad> findByActivoTrue() {
            return storage.values().stream()
                    .filter(Propiedad::getActivo)
                    .toList();
        }
    }

    // Servicio
    public static class PropiedadService {
        private final PropiedadDAO dao;

        public PropiedadService(PropiedadDAO dao) {
            this.dao = dao;
        }

        private boolean validarCampos(String titulo, String descripcion, String direccion,
                                      String ciudad, String tipoInmueble, Integer habitaciones,
                                      Integer capacidad, BigDecimal precioNoche, String politicaCancelacion) {
            if (titulo == null || titulo.trim().isEmpty() || titulo.length() > 150) return false;
            if (direccion == null || direccion.trim().isEmpty() || direccion.length() > 200) return false;
            if (ciudad == null || ciudad.trim().isEmpty() || ciudad.length() > 80) return false;
            if (tipoInmueble == null || tipoInmueble.trim().isEmpty() || tipoInmueble.length() > 50) return false;
            if (habitaciones == null || habitaciones <= 0) return false;
            if (capacidad == null || capacidad <= 0) return false;
            if (precioNoche == null || precioNoche.compareTo(BigDecimal.ZERO) <= 0) return false;
            if (politicaCancelacion == null || politicaCancelacion.trim().isEmpty() || politicaCancelacion.length() > 60) return false;
            if (descripcion != null && descripcion.length() > 10000) return false; // límite razonable
            return true;
        }

        public Propiedad registrarPropiedad(Usuario propietario, String titulo, String descripcion,
                                           String direccion, String ciudad, String tipoInmueble,
                                           Integer habitaciones, Integer capacidad, BigDecimal precioNoche,
                                           String politicaCancelacion, Boolean permiteReservaInmediata) {
            if (propietario == null || propietario.getId() == null) {
                return null;
            }
            if (!validarCampos(titulo, descripcion, direccion, ciudad, tipoInmueble,
                    habitaciones, capacidad, precioNoche, politicaCancelacion)) {
                return null;
            }
            Propiedad p = new Propiedad();
            p.setPropietario(propietario);
            p.setTitulo(titulo.trim());
            p.setDescripcion(descripcion == null ? null : descripcion.trim());
            p.setDireccion(direccion.trim());
            p.setCiudad(ciudad.trim());
            p.setTipoInmueble(tipoInmueble.trim());
            p.setHabitaciones(habitaciones);
            p.setCapacidad(capacidad);
            p.setPrecioNoche(precioNoche);
            p.setPoliticaCancelacion(politicaCancelacion.trim());
            p.setPermiteReservaInmediata(Boolean.TRUE.equals(permiteReservaInmediata));
            return dao.save(p);
        }

        public Propiedad obtenerPropiedadPorId(Long id) {
            return dao.findById(id).orElse(null);
        }

        public List<Propiedad> obtenerPropiedadesDePropietario(Long propietarioId) {
            if (propietarioId == null) return List.of();
            return dao.findByPropietarioId(propietarioId);
        }

        public List<Propiedad> listarPropiedadesActivas() {
            return dao.findByActivoTrue();
        }

        public boolean actualizarPropiedad(Long id, String titulo, String descripcion,
                                           String direccion, String ciudad, String tipoInmueble,
                                           Integer habitaciones, Integer capacidad, BigDecimal precioNoche,
                                           String politicaCancelacion, Boolean permiteReservaInmediata) {
            Propiedad existente = dao.findById(id).orElse(null);
            if (existente == null || !existente.getActivo()) {
                return false;
            }
            if (!validarCampos(titulo, descripcion, direccion, ciudad, tipoInmueble,
                    habitaciones, capacidad, precioNoche, politicaCancelacion)) {
                return false;
            }
            existente.setTitulo(titulo.trim());
            existente.setDescripcion(descripcion == null ? null : descripcion.trim());
            existente.setDireccion(direccion.trim());
            existente.setCiudad(ciudad.trim());
            existente.setTipoInmueble(tipoInmueble.trim());
            existente.setHabitaciones(habitaciones);
            existente.setCapacidad(capacidad);
            existente.setPrecioNoche(precioNoche);
            existente.setPoliticaCancelacion(politicaCancelacion.trim());
            existente.setPermiteReservaInmediata(Boolean.TRUE.equals(permiteReservaInmediata));
            dao.save(existente);
            return true;
        }
    }
}