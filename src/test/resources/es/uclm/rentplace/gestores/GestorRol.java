package es.uclm.rentplace.gestores;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GestorRol {

    public static class Usuario {
        public enum Rol { PROPIETARIO, INQUILINO }
        private Long id;
        private String username;
        private Rol rol;
        public Usuario() {}
        public Usuario(Long id, String username, Rol rol) { this.id = id; this.username = username; this.rol = rol; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public Rol getRol() { return rol; }
        public void setRol(Rol rol) { this.rol = rol; }
    }

    public interface UsuarioDAO {
        Optional<Usuario> findById(Long id);
        Usuario save(Usuario usuario);
    }

    public static class InMemoryUsuarioDAO implements UsuarioDAO {
        private final Map<Long, Usuario> storage = new HashMap<>();
        public void add(Usuario u) { storage.put(u.getId(), u); }
        @Override public Optional<Usuario> findById(Long id) { return Optional.ofNullable(storage.get(id)); }
        @Override public Usuario save(Usuario usuario) { storage.put(usuario.getId(), usuario); return usuario; }
    }

    public interface Session {
        Object getAttribute(String name);
        void setAttribute(String name, Object value);
    }

    public static class SimpleSession implements Session {
        private final Map<String, Object> map = new HashMap<>();
        @Override public Object getAttribute(String name) { return map.get(name); }
        @Override public void setAttribute(String name, Object value) { map.put(name, value); }
    }

    public interface Model {
        void addAttribute(String name, Object value);
        Object get(String name);
    }

    public static class SimpleModel implements Model {
        private final Map<String, Object> map = new HashMap<>();
        @Override public void addAttribute(String name, Object value) { map.put(name, value); }
        @Override public Object get(String name) { return map.get(name); }
    }

    private final UsuarioDAO usuarioDAO;
    public GestorRol(UsuarioDAO usuarioDAO) { this.usuarioDAO = usuarioDAO; }

    public String convertirAInquilino(Session session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "Debes iniciar sesión.");
            return "login";
        }
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "home";
        }
        usuario.setRol(Usuario.Rol.INQUILINO);
        usuarioDAO.save(usuario);
        session.setAttribute("rol", "INQUILINO");
        model.addAttribute("message", "Ahora eres inquilino. ¡Puedes buscar viviendas!");
        return "redirect:/profile";
    }
}