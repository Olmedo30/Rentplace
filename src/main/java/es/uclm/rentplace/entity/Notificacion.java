package es.uclm.rentplace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "titulo", length = 100, nullable = false)
    private String titulo;
    
    @Column(name = "mensaje", columnDefinition = "TEXT", nullable = false)
    private String mensaje;
    
    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;
    
    @Column(name = "leida", nullable = false)
    private Boolean leida;
    
    @Column(name = "tipo", length = 50, nullable = false)
    private String tipo; // RESERVA, SOLICITUD, PAGO, etc.

    public Notificacion() {}
    
    public Notificacion(Usuario usuario, String titulo, String mensaje, String tipo) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fechaEnvio = LocalDateTime.now();
        this.leida = false;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    
    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    // Métodos de negocio
    public void marcarComoLeida() {
        this.leida = true;
    }
}