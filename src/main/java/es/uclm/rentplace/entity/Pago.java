package es.uclm.rentplace.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;
    
    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;
    
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;
    
    @Column(name = "referencia", nullable = false, unique = true)
    private String referencia;
    
    @Column(name = "completado", nullable = false)
    private Boolean completado;

    public Pago() {}
    
    public Pago(Reserva reserva, BigDecimal monto, MetodoPago metodoPago) {
        this.reserva = reserva;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaPago = LocalDateTime.now();
        this.referencia = UUID.randomUUID().toString();
        this.completado = false;
    }

    // Enum para métodos de pago
    public enum MetodoPago {
        TARJETA_CREDITO,
        TARJETA_DEBITO,
        PAYPAL
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
    
    // Métodos de negocio
    public void completarPago() {
        this.completado = true;
        this.reserva.setPagado(true);
    }
    
    public void reembolsarPago() {
        this.completado = false;
        this.reserva.setPagado(false);
    }
}