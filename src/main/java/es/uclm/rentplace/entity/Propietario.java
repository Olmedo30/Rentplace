package es.uclm.rentplace.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "propietarios")
public class Propietario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefono;

    @OneToMany(mappedBy = "propietario", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
 private List<Propiedad> propiedades = new ArrayList<>();

    public Propietario() {}

    public Propietario(String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
      
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public List<Propiedad> getPropiedades() { 
    	return propiedades; 
    	}
    public void setPropiedades(List<Propiedad> propiedades) {
        this.propiedades = propiedades != null ? propiedades : new ArrayList<>();
        }
    
    // Helpers para mantener la relación bidireccional consistente
    public void addPropiedad(Propiedad propiedad) {
        propiedades.add(propiedad);
        propiedad.setPropietario(this);
    }

    public void removePropiedad(Propiedad propiedad) {
        propiedades.remove(propiedad);
        propiedad.setPropietario(null);
    }

    @Override
    public String toString() {
        return String.format("Propietario[id=%d, nombre='%s', email='%s', telefono='%s', propiedades=%d]",
                id, nombre, email, telefono, propiedades == null ? 0 : propiedades.size());
    }

    
    }
