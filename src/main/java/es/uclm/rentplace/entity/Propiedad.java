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
    
    
	
	

}
