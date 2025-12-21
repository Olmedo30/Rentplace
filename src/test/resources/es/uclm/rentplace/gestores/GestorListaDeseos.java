package es.uclm.rentplace.gestores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class GestorListaDeseos {

    //Entidades internas 

    public static class Usuario {
        public enum Rol { INQUILINO, PROPIETARIO }

        private final Long id;
        private final Rol rol;

        public Usuario(Long id, Rol rol) {
            this.id = id;
            this.rol = rol;
        }

        public Long getId() { return id; }
        public Rol getRol() { return rol; }
    }

    public static class Propiedad {
        private final Long id;

        public Propiedad(Long id) {
            this.id = id;
        }

        public Long getId() { return id; }
    }

    public static class ListaDeseos {
        private final Usuario usuario;
        private final List<Propiedad> propiedades = new ArrayList<>();

        public ListaDeseos(Usuario usuario) {
            this.usuario = usuario;
        }

        public Usuario getUsuario() { return usuario; }
        public List<Propiedad> getPropiedades() { return new ArrayList<>(propiedades); }

        public void agregarPropiedad(Propiedad p) {
            if (!propiedades.contains(p)) {
                propiedades.add(p);
            }
        }

        public void eliminarPropiedad(Propiedad p) {
            propiedades.remove(p);
        }

        public boolean contienePropiedad(Long propiedadId) {
            return propiedades.stream().anyMatch(p -> p.getId().equals(propiedadId));
        }
    }

    //DAO 

    public interface ListaDeseosDAO {
        ListaDeseos save(ListaDeseos lista);
        Optional<ListaDeseos> findByUsuarioId(Long usuarioId);
        boolean existsByUsuarioId(Long usuarioId);
    }

    public static class InMemoryListaDeseosDAO implements ListaDeseosDAO {
        private final Map<Long, ListaDeseos> storage = new ConcurrentHashMap<>();

        @Override
        public ListaDeseos save(ListaDeseos lista) {
            storage.put(lista.getUsuario().getId(), lista);
            return lista;
        }

        @Override
        public Optional<ListaDeseos> findByUsuarioId(Long usuarioId) {
            return Optional.ofNullable(storage.get(usuarioId));
        }

        @Override
        public boolean existsByUsuarioId(Long usuarioId) {
            return storage.containsKey(usuarioId);
        }
    }

    //Servicio 

    public static class ListaDeseosService {
        private final ListaDeseosDAO listaDeseosDAO;
        private final java.util.function.Function<Long, Usuario> usuarioProvider;
        private final java.util.function.Function<Long, Propiedad> propiedadProvider;

        public ListaDeseosService(
                ListaDeseosDAO listaDeseosDAO,
                java.util.function.Function<Long, Usuario> usuarioProvider,
                java.util.function.Function<Long, Propiedad> propiedadProvider) {
            this.listaDeseosDAO = listaDeseosDAO;
            this.usuarioProvider = usuarioProvider;
            this.propiedadProvider = propiedadProvider;
        }

        private boolean esInquilino(Long usuarioId) {
            Usuario u = usuarioProvider.apply(usuarioId);
            return u != null && u.getRol() == Usuario.Rol.INQUILINO;
        }

        public boolean agregarPropiedadALista(Long usuarioId, Long propiedadId) {
            // Validar que el usuario existe y es inquilino
            if (!esInquilino(usuarioId)) {
                return false;
            }

            // Validar que la propiedad existe
            Propiedad propiedad = propiedadProvider.apply(propiedadId);
            if (propiedad == null) {
                return false;
            }

            // Obtener o crear lista
            ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario u = usuarioProvider.apply(usuarioId);
                    return new ListaDeseos(u);
                });

            // Evitar duplicados
            if (lista.contienePropiedad(propiedadId)) {
                return true; // ya estaba, pero no es error
            }

            lista.agregarPropiedad(propiedad);
            listaDeseosDAO.save(lista);
            return true;
        }

        public boolean eliminarPropiedadDeLista(Long usuarioId, Long propiedadId) {
            // Solo inquilinos pueden tener lista, pero si no existe, falla silenciosamente
            if (!esInquilino(usuarioId)) {
                return false;
            }

            Propiedad propiedad = propiedadProvider.apply(propiedadId);
            if (propiedad == null) {
                return false;
            }

            Optional<ListaDeseos> listaOpt = listaDeseosDAO.findByUsuarioId(usuarioId);
            if (listaOpt.isEmpty()) {
                return false; // no hay lista → no se puede eliminar
            }

            ListaDeseos lista = listaOpt.get();
            if (!lista.contienePropiedad(propiedadId)) {
                return false; // no estaba → consideramos "fallo" o al menos no éxito
            }

            lista.eliminarPropiedad(propiedad);
            listaDeseosDAO.save(lista);
            return true;
        }

        public List<Propiedad> obtenerPropiedadesDeLista(Long usuarioId) {
            if (!esInquilino(usuarioId)) {
                return List.of();
            }
            return listaDeseosDAO.findByUsuarioId(usuarioId)
                    .map(ListaDeseos::getPropiedades)
                    .orElse(List.of());
        }

        public boolean estaEnListaDeDeseos(Long usuarioId, Long propiedadId) {
            if (!esInquilino(usuarioId)) {
                return false;
            }
            return listaDeseosDAO.findByUsuarioId(usuarioId)
                    .map(lista -> lista.contienePropiedad(propiedadId))
                    .orElse(false);
        }
    }
}