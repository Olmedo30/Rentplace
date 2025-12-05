package es.uclm.rentplace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitudes_reserva")
public class SolicitudReserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;
    
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;
    
    @Column(name = "confirmada", nullable = false)
    private Boolean confirmada;
    
    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;
    
    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    public SolicitudReserva() {}
    
    public SolicitudReserva(Reserva reserva) {
        this.reserva = reserva;
        this.fechaSolicitud = LocalDateTime.now();
        this.confirmada = false;
        this.sessionId = UUID.randomUUID().toString();
    }

    // Getters y setters
    public Long getId() {
    	return id;
    }
    public void setId(Long id) { 
    	this.id = id;
    }
    
    public Reserva getReserva() { 
    	return reserva; 
    }
    public void setReserva(Reserva reserva) { 
    	this.reserva = reserva;
    }
    
    public LocalDateTime getFechaSolicitud() { 
    	return fechaSolicitud;
    }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { 
    	this.fechaSolicitud = fechaSolicitud; 
    }
    
    public Boolean getConfirmada() { 
    	return confirmada; 
    }
    public void setConfirmada(Boolean confirmada) { 
    	this.confirmada = confirmada; 
    }
    
    public LocalDateTime getFechaConfirmacion() { 
    	return fechaConfirmacion; 
    }
    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) { 
    	this.fechaConfirmacion = fechaConfirmacion; 
    }
    public String getSessionId() { 
    	return sessionId; 
    }
    public void setSessionId(String sessionId) { 
    	this.sessionId = sessionId; 
    }
    // Métodos de negocio
    public void confirmarSolicitud() {
        this.confirmada = true;
        this.fechaConfirmacion = LocalDateTime.now();
        this.reserva.confirmarReserva();
    }
    
    public void rechazarSolicitud() {
        this.confirmada = false;
        this.fechaConfirmacion = LocalDateTime.now();
        this.reserva.cancelarReserva();
    }
}