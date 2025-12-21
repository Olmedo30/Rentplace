package es.uclm.rentplace.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BusquedaPropiedadDTO {
    
    private String ciudad = "";
    private String tipoInmueble = "";
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private BigDecimal precioMaximo;
    private Integer capacidadMinima;
    
    // Constructor vacío
    public BusquedaPropiedadDTO() {
    }
    
    // Getters y setters
    public String getCiudad() {
        if (ciudad != null) {
            return ciudad;
        } else {
            return "";
        }
    }

    public void setCiudad(String ciudad) {
        if (ciudad != null) {
            this.ciudad = ciudad;
        } else {
            this.ciudad = "";
        }
    }
    
    public String getTipoInmueble() {
        if (tipoInmueble != null) {
            return tipoInmueble;
        } else {
            return "";
        }
    }

    public void setTipoInmueble(String tipoInmueble) {
        if (tipoInmueble != null) {
            this.tipoInmueble = tipoInmueble;
        } else {
            this.tipoInmueble = "";
        }
    }
    
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
    
    public BigDecimal getPrecioMaximo() {
        return precioMaximo;
    }
    
    public void setPrecioMaximo(BigDecimal precioMaximo) {
        this.precioMaximo = precioMaximo;
    }
    public Integer getCapacidadMinima() {
        return capacidadMinima;
    }
    
    public void setCapacidadMinima(Integer capacidadMinima) {
        this.capacidadMinima = capacidadMinima;
    }
}