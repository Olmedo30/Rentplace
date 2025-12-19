package es.uclm.rentplace.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Column(nullable = false)
    private boolean propietario;

    // Constructor vacío (obligatorio para JPA)
    public Usuario() {}

    // Constructor con parámetros (opcional, útil para pruebas o creación)
    public Usuario(String username, String password, String email, String telefono, boolean propietario) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.telefono = telefono;
        this.propietario = propietario;
    }

    // Getters y Setters
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

    public boolean isPropietario() { return propietario; }
    public void setPropietario(boolean propietario) { this.propietario = propietario; }

    @Override
    public String toString() {
        return String.format("Usuario[id=%d, username='%s', email='%s', telefono='%s', propietario='%b']",
                id, username, email, telefono, propietario);
    }
}