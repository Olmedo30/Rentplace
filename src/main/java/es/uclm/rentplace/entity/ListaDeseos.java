package es.uclm.rentplace.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listas_deseos")
public class ListaDeseos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    
    @ManyToMany
    @JoinTable(
        name = "lista_deseos_propiedades",
        joinColumns = @JoinColumn(name = "lista_deseos_id"),
        inverseJoinColumns = @JoinColumn(name = "propiedad_id")
    )
    private List<Propiedad> propiedades = new ArrayList<>();

    public ListaDeseos() {}
    
    public ListaDeseos(Usuario usuario) {
        this.usuario = usuario;
        this.propiedades = new ArrayList<>();
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public List<Propiedad> getPropiedades() { return propiedades; }
    public void setPropiedades(List<Propiedad> propiedades) { this.propiedades = propiedades; }
    
    // Métodos para gestionar propiedades
    public void agregarPropiedad(Propiedad propiedad) {
        if (!propiedades.contains(propiedad)) {
            propiedades.add(propiedad);
        }
    }
    
    public void eliminarPropiedad(Propiedad propiedad) {
        propiedades.remove(propiedad);
    }
}