package es.uclm.rentplace.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Propiedad {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "propiedades_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;
	
	@Column(name = "titulo", length = 150, nullable = false)
    private String titulo;
	
	@Lob
	@Column(name = "descripcion", columnDefinition = "TEXT")
	private String descripcion;
	
	@Column(name = "direccion", length = 200)
	private String direccion;
	
	@Column(name = "ciudad", length = 80)
    private String ciudad;
	
    @Column(name = "tipo_inmueble", length = 50)
    private String tipoInmueble;

    @Column(name = "habitaciones")
    private Integer habitaciones;

    @Column(name = "capacidad")
    private Integer capacidad;

    @Column(name = "precio_noche", precision = 10, scale = 2)
    private BigDecimal precioNoche;
    
    @Column(name = "politica cancelación", length = 60)
    private String politicaCancelacion;
    
    @Column(name = "permite_reserva_inmediata")
    private Boolean permiteReservaInmediata;
    
    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;
    
    @Column(name = "activo")
    private Boolean activo;
    
    public Propiedad() {}
    
    public Propiedad(Usuario propietario, String titulo, String descripcion, String direccion,
            String ciudad, String tipoInmueble, Integer habitaciones, Integer capacidad,
            BigDecimal precioNoche, String politicaCancelacion, Boolean permiteReservaInmediata,
            LocalDateTime fechaAlta, Boolean activo) {
    	this.propietario = propietario;
    	this.titulo = titulo;
    	this.descripcion = descripcion;
    	this.direccion = direccion;
    	this.ciudad = ciudad; 
    	this.tipoInmueble = tipoInmueble;
    	this.habitaciones = habitaciones;
    	this.capacidad = capacidad;
    	this.precioNoche = precioNoche;
    	this.politicaCancelacion = this.politicaCancelacion;
    	this.permiteReservaInmediata = permiteReservaInmediata;
    	this.fechaAlta = fechaAlta;
    	this.activo = activo;
    }
    
    
    public Long getId() {
    	return id; 	
    }
    public void setId(Long id) { 
    	this.id = id; 
    }
    
    public Usuario getPropietario() {
    	return propietario;
    }
    public void setPropietario(Usuario propietario) { 
    	this.propietario = propietario; 
    }
    
    public String getTitulo() { 
    	return titulo; 
    }
    public void setTitulo(String titulo) {
    	this.titulo = titulo;
    }
    
    public String getDescripcion() { 
    	return descripcion;
    }
    public void setDescripcion(String descripcion) {
    	this.descripcion = descripcion;
    }
    
    public String getDireccion() {
    	return direccion;
    }
    public void setDireccion(String direccion) {
    	this.direccion = direccion;
    }
    
    public String getCiudad() {
    	return ciudad;
    }
    public void setCiudad(String ciudad) {
    	this.ciudad = ciudad;
    }
    
    public String getTipoInmueble() {
    	return tipoInmueble;
    }
    public void setTipoInmueble(String tipoInmueble) {
    	this.tipoInmueble = tipoInmueble;
    }
    
    public Integer getHabitaciones() {
    	return habitaciones;
    }
    public void setHabitaciones(Integer habitaciones) {
    	this.habitaciones = habitaciones;
    }
    
    public Integer getCapacidad() { 
    	return capacidad; 
    }
    public void setCapacidad(Integer capacidad) { 
    	this.capacidad = capacidad; 
    }

    public BigDecimal getPrecioNoche() { 
    	return precioNoche; 
    }
    public void setPrecioNoche(BigDecimal precioNoche) {
    	this.precioNoche = precioNoche; 
    }

    public String getPoliticaCancelacion() { 
    	return politicaCancelacion; 
    }
    public void setPoliticaCancelacion(String politicaCancelacion) {
    	this.politicaCancelacion = politicaCancelacion; 
    }

    public Boolean getPermiteReservaInmediata() { 
    	return permiteReservaInmediata; 
    }
    public void setPermiteReservaInmediata(Boolean permiteReservaInmediata) { 
    	this.permiteReservaInmediata = permiteReservaInmediata; 
    }

    public LocalDateTime getFechaAlta() { 
    	return fechaAlta; 
    }
    public void setFechaAlta(LocalDateTime fechaAlta) { 
    	this.fechaAlta = fechaAlta; 
    }

    public Boolean getActivo() { 
    	return activo; 
    }
    public void setActivo(Boolean activo) { 
    	this.activo = activo; 
    }
    
    @PrePersist
    
    protected void onCreate() {
    	if(this.fechaAlta == null) {
    		this.fechaAlta = LocalDateTime.now();
    	}
    	if(this.activo == null) {
    		this.activo = true;
    		
    	}
    }
    
    @Override
    public String toString() {
    	return String.format("Propiedad[id=%d, titulo='%s', ciudad='%s', precio=%s, activo=%s]",
    			id, titulo, ciudad, precioNoche, activo);
    	}
    	
    }
    
 
