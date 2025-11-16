package es.uclm.rentplace.entity;

// Importamos las anotaciones JPA necesarias para mapear la clase a la base de datos
import jakarta.persistence.*;

// Importamos BigDecimal para poder manejar dinero de forma precisa
import java.math.BigDecimal;

// Impostamos LocalDateTime para guardar la fecha en el que se crea la propiedad
import java.time.LocalDateTime;

@Entity
public class Propiedad {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "propiedades_id")
	private Long id;
	
	// // Relación N:1 con Usuario: un propietario puede tener varias propiedades)
	@ManyToOne(fetch = FetchType.LAZY)// LAZY evita cargar el usuario hasta que se necesite
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;
	
	@Column(name = "titulo", length = 150, nullable = false)// "nullable = false" campo obligatorio
    private String titulo;
	
	@Lob // Indica que es un texto largo (Large Object)
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
    private BigDecimal precioNoche; // BigDecimal es la opción correcta para que los precios sean de forma precisa
    
    @Column(name = "politica cancelación", length = 60)
    private String politicaCancelacion;
    
    @Column(name = "permite_reserva_inmediata")
    private Boolean permiteReservaInmediata;
    
    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;
    
    @Column(name = "activo")
    private Boolean activo;
    
    // Es necesario crear un constructor vacío para que JPA pueda crear objetos
    public Propiedad() {}
    
    // Constructor con parámetros
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
    
    // Getters y setters
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
    
    // Este método se ejecuta de forma automática antes de insertar la entidad en la BD
    @PrePersist
    
    protected void onCreate() {
    	if(this.fechaAlta == null) {
    		this.fechaAlta = LocalDateTime.now();// Fecha actual de creación
    	}
    	if(this.activo == null) {
    		this.activo = true; // Las propiedades nuevas empiezan activas
    		
    	}
    }
    // Representación de los atributos
    @Override
    public String toString() {
    	return String.format("Propiedad[id=%d, titulo='%s', ciudad='%s', precio=%s, activo=%s]",
    			id, titulo, ciudad, precioNoche, activo);
    	}
    	
    }
    
 
