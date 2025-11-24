package es.uclm.rentplace.entity;

import jakarta.persistence.*;

// import java.util.ArrayList;
// import java.util.List;

@Entity
@Table(name = "inquilinos")
public class Inquilino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    // Futuras relaciones (comentadas hasta que implementes Vivienda/Reserva)
    // @OneToMany(mappedBy = "inquilino")
    // private List<Vivienda> listaDeseos = new ArrayList<>();

    // @OneToMany(mappedBy = "inquilino")
    // private List<Reserva> reservas = new ArrayList<>();

    public Inquilino() {}

    public Inquilino(Usuario usuario) {
        this.usuario = usuario;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // Métodos futuros (descomentar cuando implementes Vivienda/Reserva)
    // public List<Vivienda> getListaDeseos() { return listaDeseos; }
    // public List<Reserva> getReservas() { return reservas; }
}