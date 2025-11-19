package es.uclm.rentplace.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "disponibilidades")
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disponibilidades_id")
    private Long id;

    // Clave foránea a la tabla propiedades (columna propiedad_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad;

    @Column(name = "fecha_inicio", columnDefinition = "DATE")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", columnDefinition = "DATE")
    private LocalDate fechaFin;

    @Column(name = "disponible")
    private Boolean disponible;

    @Column(name = "precio_especial", precision = 10, scale = 2)
    private BigDecimal precioEspecial;

    public Disponibilidad() {}

    public Disponibilidad(Propiedad propiedad, LocalDate fechaInicio, LocalDate fechaFin, Boolean disponible, BigDecimal precioEspecial) {
        this.propiedad = propiedad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.disponible = disponible;
        this.precioEspecial = precioEspecial;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Propiedad getPropiedad() { return propiedad; }
    public void setPropiedad(Propiedad propiedad) { this.propiedad = propiedad; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public BigDecimal getPrecioEspecial() { return precioEspecial; }
    public void setPrecioEspecial(BigDecimal precioEspecial) { this.precioEspecial = precioEspecial; }

    @Override
    public String toString() {
        return String.format("Disponibilidad[id=%d propiedadId=%s %s -> %s disponible=%s]",
                id,
                propiedad == null ? "null" : String.valueOf(propiedad.getId()),
                fechaInicio, fechaFin,
                disponible);
    }
}
