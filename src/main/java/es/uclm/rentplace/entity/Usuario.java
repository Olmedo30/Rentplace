package es.uclm.rentplace.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private String direccion;

    // Campo rol (reemplaza a Propietario e Inquilino)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // Relación con ListaDeseos (opcional, solo para inquilinos)
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private ListaDeseos listaDeseos;

    // Relación con Propiedades (para propietarios)
    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    private List<Propiedad> propiedades = new ArrayList<>();

    // Constructor vacío
    public Usuario() {}

    // Constructor con parámetros
    public Usuario(String username, String password, String email, String telefono, 
                  String nombre, String apellidos, String direccion, Rol rol) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.telefono = telefono;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.rol = rol;
    }

    // Enum para roles
    public enum Rol {
        PROPIETARIO,
        INQUILINO
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public ListaDeseos getListaDeseos() { return listaDeseos; }
    public void setListaDeseos(ListaDeseos listaDeseos) { this.listaDeseos = listaDeseos; }

    public List<Propiedad> getPropiedades() { return propiedades; }
    public void setPropiedades(List<Propiedad> propiedades) { this.propiedades = propiedades; }

    @Override
    public String toString() {
        return String.format("Usuario[id=%d, username='%s', email='%s', rol='%s']",
                id, username, email, rol);
    }
}