package es.uclm.rentplace.gestores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GestorBusqueda {

    public static class BusquedaPropiedadDTO {
        private String ciudad = "";
        private String tipoInmueble = "";
        private LocalDate fechaEntrada;
        private LocalDate fechaSalida;
        private BigDecimal precioMaximo;
        private Integer capacidadMinima;
        public BusquedaPropiedadDTO() {}
        public String getCiudad() { return ciudad != null ? ciudad : ""; }
        public void setCiudad(String ciudad) { this.ciudad = ciudad != null ? ciudad : ""; }
        public String getTipoInmueble() { return tipoInmueble != null ? tipoInmueble : ""; }
        public void setTipoInmueble(String tipoInmueble) { this.tipoInmueble = tipoInmueble != null ? tipoInmueble : ""; }
        public LocalDate getFechaEntrada() { return fechaEntrada; }
        public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }
        public LocalDate getFechaSalida() { return fechaSalida; }
        public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
        public BigDecimal getPrecioMaximo() { return precioMaximo; }
        public void setPrecioMaximo(BigDecimal precioMaximo) { this.precioMaximo = precioMaximo; }
        public Integer getCapacidadMinima() { return capacidadMinima; }
        public void setCapacidadMinima(Integer capacidadMinima) { this.capacidadMinima = capacidadMinima; }
    }

    public static class Propiedad {
        private Long id;
        private String tipoInmueble;
        private String ciudad;
        private int capacidad;
        private BigDecimal precioNoche;
        private boolean activo;
        public Propiedad(Long id, String tipoInmueble, String ciudad, int capacidad, BigDecimal precioNoche, boolean activo) {
            this.id = id;
            this.tipoInmueble = tipoInmueble;
            this.ciudad = ciudad;
            this.capacidad = capacidad;
            this.precioNoche = precioNoche;
            this.activo = activo;
        }
        public Long getId() { return id; }
        public String getTipoInmueble() { return tipoInmueble; }
        public String getCiudad() { return ciudad; }
        public int getCapacidad() { return capacidad; }
        public BigDecimal getPrecioNoche() { return precioNoche; }
        public boolean isActivo() { return activo; }
    }

    public interface PropiedadDAO {
        List<Propiedad> findByActivoTrue();
        List<String> findDistinctTipoInmuebleByActivoTrue();
        List<String> findDistinctCiudadByActivoTrue();
    }

    public static class InMemoryPropiedadDAO implements PropiedadDAO {
        private final List<Propiedad> datos = new ArrayList<>();
        public InMemoryPropiedadDAO(List<Propiedad> inicial) { if (inicial != null) datos.addAll(inicial); }
        @Override public List<Propiedad> findByActivoTrue() { return datos.stream().filter(Propiedad::isActivo).collect(Collectors.toList()); }
        @Override public List<String> findDistinctTipoInmuebleByActivoTrue() { return datos.stream().filter(Propiedad::isActivo).map(Propiedad::getTipoInmueble).distinct().collect(Collectors.toList()); }
        @Override public List<String> findDistinctCiudadByActivoTrue() { return datos.stream().filter(Propiedad::isActivo).map(Propiedad::getCiudad).distinct().collect(Collectors.toList()); }
    }

    public static class Page<T> {
        private final List<T> content;
        private final int totalPages;
        private final long totalElements;
        private final int page;
        private final int size;
        public Page(List<T> content, int page, int size, long totalElements) {
            this.content = List.copyOf(content);
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = size == 0 ? 0 : (int) ((totalElements + size - 1) / size);
        }
        public List<T> getContent() { return content; }
        public int getTotalPages() { return totalPages; }
        public long getTotalElements() { return totalElements; }
        public int getPageNumber() { return page; }
        public int getPageSize() { return size; }
    }

    public static class BusquedaService {
        private final PropiedadDAO propiedadDAO;
        public BusquedaService(PropiedadDAO propiedadDAO) { this.propiedadDAO = propiedadDAO; }
        public Page<Propiedad> buscarPropiedades(BusquedaPropiedadDTO criterios, int page, int size) {
            List<Propiedad> todasPropiedades = propiedadDAO.findByActivoTrue();
            List<Propiedad> propiedadesFiltradas = filtrarPropiedades(todasPropiedades, criterios);
            int start = page * size;
            int end = Math.min(start + size, propiedadesFiltradas.size());
            if (start > end || start >= propiedadesFiltradas.size()) {
                return new Page<>(List.of(), page, size, propiedadesFiltradas.size());
            }
            List<Propiedad> pageContent = propiedadesFiltradas.subList(start, end);
            return new Page<>(pageContent, page, size, propiedadesFiltradas.size());
        }
        private List<Propiedad> filtrarPropiedades(List<Propiedad> propiedades, BusquedaPropiedadDTO criterios) {
            Stream<Propiedad> stream = propiedades.stream();
            if (criterios.getTipoInmueble() != null && !criterios.getTipoInmueble().isEmpty()) {
                stream = stream.filter(p -> criterios.getTipoInmueble().equals(p.getTipoInmueble()));
            }
            if (criterios.getCiudad() != null && !criterios.getCiudad().isEmpty()) {
                stream = stream.filter(p -> p.getCiudad().toLowerCase().contains(criterios.getCiudad().toLowerCase()));
            }
            if (criterios.getCapacidadMinima() != null) {
                stream = stream.filter(p -> p.getCapacidad() >= criterios.getCapacidadMinima());
            }
            if (criterios.getPrecioMaximo() != null) {
                stream = stream.filter(p -> p.getPrecioNoche().compareTo(criterios.getPrecioMaximo()) <= 0);
            }
            return stream.collect(Collectors.toList());
        }
        public List<String> obtenerTiposInmuebleDisponibles() { return propiedadDAO.findDistinctTipoInmuebleByActivoTrue(); }
        public List<String> obtenerCiudadesDisponibles() { return propiedadDAO.findDistinctCiudadByActivoTrue(); }
    }

    private final BusquedaService service;
    public GestorBusqueda(BusquedaService service) { this.service = service; }
    public Page<Propiedad> buscar(BusquedaPropiedadDTO criterios, int page, int size) { return service.buscarPropiedades(criterios, page, size); }
    public List<String> tiposDisponibles() { return service.obtenerTiposInmuebleDisponibles(); }
    public List<String> ciudadesDisponibles() { return service.obtenerCiudadesDisponibles(); }
}
