package es.uclm.rentplace.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inquilinos")
public class Inquilino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefono;

    // Relación con Usuario (uno a uno, ya que un usuario puede ser inquilino)
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    // Constructores
    public Inquilino() {}

    public Inquilino(String nombre, String email, String telefono, Usuario usuario) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    public String toString() {
        return String.format("Inquilino[id=%d, nombre='%s', email='%s', telefono='%s', usuario='%s']",
                id, nombre, email, telefono, usuario != null ? usuario.getUsername() : "sin usuario");
    }
}