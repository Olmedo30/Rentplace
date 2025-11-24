package es.uclm.rentplace.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Long alojamientoId; 

    @Column(nullable = false)
    private LocalDate fechaEntrada;

    @Column(nullable = false)
    private LocalDate fechaSalida;

    @Column(nullable = false)
    private Double precioTotal;
    
    @Column(nullable = false)
    private String estado; // Pendiente, Pagada, Cancelada

    // Constructor vacío (obligatorio para JPA)
    public Reserva() {}

    // Constructor con parámetros para la creación
    public Reserva(Usuario usuario, Long alojamientoId, LocalDate fechaEntrada, LocalDate fechaSalida, Double precioTotal) {
        this.usuario = usuario;
        this.alojamientoId = alojamientoId;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.precioTotal = precioTotal;
        this.estado = "PENDIENTE"; // Estado inicial
    }

    // Getters y Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getAlojamientoId() {
        return alojamientoId;
    }

    public void setAlojamientoId(Long alojamientoId) {
        this.alojamientoId = alojamientoId;
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

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getUsername() : "N/A") +
                ", alojamientoId=" + alojamientoId +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", precioTotal=" + precioTotal +
                ", estado='" + estado + '\'' +
                '}';
    }
}