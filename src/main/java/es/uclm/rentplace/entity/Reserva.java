package es.uclm.rentplace.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inquilino_id", nullable = false)
    private Usuario inquilino;
    
    @ManyToOne
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDateTime fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDateTime fechaSalida;

    @Enumerated(EnumType.STRING)
    @Column(name = "politica_cancelacion", nullable = false)
    private PoliticaCancelacion politicaCancelacion;
    
    @Column(name = "reserva_confirmada", nullable = false)
    private Boolean reservaConfirmada;
    
    @Column(name = "pagado", nullable = false)
    private Boolean pagado;
    
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private Pago pago;
    
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private SolicitudReserva solicitudReserva;

    // Constructor vacío (obligatorio para JPA)
    public Reserva() {}

    // Constructor con parámetros para la creación
    public Reserva(Usuario inquilino, Propiedad propiedad, LocalDateTime fechaEntrada, 
            LocalDateTime fechaSalida, PoliticaCancelacion politicaCancelacion) {
  this.inquilino = inquilino;
  this.propiedad = propiedad;
  this.fechaEntrada = fechaEntrada;
  this.fechaSalida = fechaSalida;
  this.politicaCancelacion = politicaCancelacion;
  this.reservaConfirmada = false;
  this.pagado = false;
}
    public BigDecimal getImporteTotal() {
        return calcularTotal();
    }
public BigDecimal calcularTotal() {
    long dias = java.time.Duration.between(fechaEntrada, fechaSalida).toDays();
    return propiedad.getPrecioNoche().multiply(BigDecimal.valueOf(dias));
}
    
    //Enum para la política de cancelación (modificable)
    public enum PoliticaCancelacion {
        NO_REEMBOLSABLE,
        REEMBOLSABLE,
        REEMBOLSABLE_50_PER
    }
    
    // Getters y Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getInquilino() {
        return inquilino;
    }

    public void setInquilino(Usuario inquilino) {
        this.inquilino = inquilino;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public PoliticaCancelacion getPoliticaCancelacion() { 
    	return politicaCancelacion; 
    }
    public void setPoliticaCancelacion(PoliticaCancelacion politicaCancelacion) {
    	this.politicaCancelacion = politicaCancelacion; 
    }
    
    public Boolean getReservaConfirmada() {
    	return reservaConfirmada; 
    }
    public void setReservaConfirmada(Boolean reservaConfirmada) { 
    	this.reservaConfirmada = reservaConfirmada; 
    }
    
    public Boolean getPagado() {
    	return pagado; 
    }
    public void setPagado(Boolean pagado) {
    	this.pagado = pagado; 
    }
    
    public Pago getPago() { 
    	return pago;
    }
    public void setPago(Pago pago) {
    	this.pago = pago;
    }
    
    public SolicitudReserva getSolicitudReserva() {
    	return solicitudReserva; 
    }
    public void setSolicitudReserva(SolicitudReserva solicitudReserva) { 
    	this.solicitudReserva = solicitudReserva;
    }
    
    // Métodos de negocio
    public boolean isActiva() {
        return LocalDateTime.now().isBefore(fechaSalida);
    }
    
    public boolean isPagado() {
        return pagado != null && pagado;
    }
    
    public void confirmarReserva() {
        this.reservaConfirmada = true;
    }
    
    public void cancelarReserva() {
        this.reservaConfirmada = false;
    }
    
    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", usuario=" + (inquilino != null ? inquilino.getUsername() : "N/A") +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", pago=" + pago +
                ", pagado='" + pagado + '\'' +
                '}';
    }

	public void setPropiedadId(Long propiedadId) {
		// TODO Auto-generated method stub
		
	}
}