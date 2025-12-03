package es.uclm.rentplace.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BusquedaPropiedadDTO {
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private String tipoInmueble = "";
    private String ciudad = "";
    private Integer capacidadMinima;
    private BigDecimal precioMaximo;

    // Constructor vacío
    public BusquedaPropiedadDTO() {
        this.tipoInmueble = "";
        this.ciudad = "";
    }

    // Getters y setters
    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getTipoInmueble() {
        return tipoInmueble != null ? tipoInmueble : "";
    }

    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble != null ? tipoInmueble : "";
    }

    public String getCiudad() {
        return ciudad != null ? ciudad : "";
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad != null ? ciudad : "";
    }

    public Integer getCapacidadMinima() {
        return capacidadMinima;
    }

    public void setCapacidadMinima(Integer capacidadMinima) {
        this.capacidadMinima = capacidadMinima;
    }

    public BigDecimal getPrecioMaximo() {
        return precioMaximo;
    }

    public void setPrecioMaximo(BigDecimal precioMaximo) {
        this.precioMaximo = precioMaximo;
    }
}