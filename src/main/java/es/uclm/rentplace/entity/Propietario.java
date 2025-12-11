package es.uclm.rentplace.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "propietarios")
public class Propietario {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "propietario", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Propiedad> propiedades = new ArrayList<>();

    public Propietario() {}

    public Propietario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // Delegates for user info
    public String getNombre() { return usuario != null ? usuario.getUsername() : null; }
    public String getEmail() { return usuario != null ? usuario.getEmail() : null; }
    public String getTelefono() { return usuario != null ? usuario.getTelefono() : null; }

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
        return String.format("Propietario[id=%d, usuario='%s', propiedades=%d]",
                id, usuario != null ? usuario.getUsername() : "null", propiedades == null ? 0 : propiedades.size());
    }
}