package es.uclm.rentplace.gestores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GestorUsuario {

    public static class Usuario {
        public enum Rol { PROPIETARIO, INQUILINO }
        private Long id;
        private String username;
        private String password;
        private String email;
        private String telefono;
        private Rol rol;
        public Usuario() {}
        public Usuario(Long id, String username, String password, String email, String telefono, Rol rol) {
            this.id = id; this.username = username; this.password = password; this.email = email; this.telefono = telefono; this.rol = rol;
        }
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
        public Rol getRol() { return rol; }
        public void setRol(Rol rol) { this.rol = rol; }
    }

    public interface UsuarioDAO {
        Optional<Usuario> findById(Long id);
        Optional<Usuario> findByUsername(String username);
        Optional<Usuario> findByEmail(String email);
        boolean existsByUsername(String username);
        boolean existsByEmail(String email);
        Usuario save(Usuario usuario);
        List<Usuario> findAll();
        List<Usuario> findByRol(Usuario.Rol rol);
    }

    public static class InMemoryUsuarioDAO implements UsuarioDAO {
        private final Map<Long, Usuario> storage = new HashMap<>();
        private long seq = 1;
        public InMemoryUsuarioDAO() {}
        public void preload(Usuario u) {
            if (u.getId() == null) u.setId(seq++);
            storage.put(u.getId(), u);
        }
        @Override public Optional<Usuario> findById(Long id) { return Optional.ofNullable(storage.get(id)); }
        @Override public Optional<Usuario> findByUsername(String username) { return storage.values().stream().filter(u -> username.equals(u.getUsername())).findFirst(); }
        @Override public Optional<Usuario> findByEmail(String email) { return storage.values().stream().filter(u -> email.equals(u.getEmail())).findFirst(); }
        @Override public boolean existsByUsername(String username) { return findByUsername(username).isPresent(); }
        @Override public boolean existsByEmail(String email) { return findByEmail(email).isPresent(); }
        @Override public Usuario save(Usuario usuario) {
            if (usuario.getId() == null) usuario.setId(seq++);
            storage.put(usuario.getId(), usuario);
            return usuario;
        }
        @Override public List<Usuario> findAll() { return new ArrayList<>(storage.values()); }
        @Override public List<Usuario> findByRol(Usuario.Rol rol) { var list = new ArrayList<Usuario>(); for (var u: storage.values()) if (u.getRol() == rol) list.add(u); return list; }
    }

    public interface PasswordEncoder {
        String encode(String raw);
        boolean matches(String raw, String encoded);
    }

    public static class SimplePasswordEncoder implements PasswordEncoder {
        @Override public String encode(String raw) { return raw == null ? null : "enc:" + raw; }
        @Override public boolean matches(String raw, String encoded) { return encoded != null && encoded.equals(encode(raw)); }
    }

    public static class UsuarioService {
        private final UsuarioDAO usuarioPersistence;
        private final PasswordEncoder passwordEncoder;
        public UsuarioService(UsuarioDAO usuarioPersistence, PasswordEncoder passwordEncoder) { this.usuarioPersistence = usuarioPersistence; this.passwordEncoder = passwordEncoder; }
        public boolean registrarUsuario(String username, String password, String email, String telefono, Usuario.Rol rol) {
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty() || email == null || email.trim().isEmpty() || telefono == null || telefono.trim().isEmpty() || rol == null) {
                return false;
            }
            if (usuarioPersistence.existsByUsername(username)) return false;
            if (usuarioPersistence.existsByEmail(email)) return false;
            String passwordEncriptada = passwordEncoder.encode(password);
            Usuario usuario = new Usuario(null, username, passwordEncriptada, email, telefono, rol);
            usuarioPersistence.save(usuario);
            return true;
        }
        public boolean autenticar(String username, String passwordPlana) {
            var usuarioOpt = usuarioPersistence.findByUsername(username);
            if (usuarioOpt.isEmpty()) return false;
            var usuario = usuarioOpt.get();
            return passwordEncoder.matches(passwordPlana, usuario.getPassword());
        }
        public Usuario obtenerPorId(Long id) { return usuarioPersistence.findById(id).orElse(null); }
        public Usuario obtenerPorUsername(String username) { return usuarioPersistence.findByUsername(username).orElse(null); }
        public boolean actualizarPerfil(Long id, String email, String telefono, String nombre, String apellidos, String direccion) {
            Usuario usuario = usuarioPersistence.findById(id).orElse(null);
            if (usuario == null) return false;
            if (!usuario.getEmail().equals(email) && usuarioPersistence.existsByEmail(email)) return false;
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuarioPersistence.save(usuario);
            return true;
        }
        public boolean cambiarContrasena(Long id, String passwordActual, String nuevaPassword) {
            Usuario usuario = usuarioPersistence.findById(id).orElse(null);
            if (usuario == null) return false;
            if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) return false;
            String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
            usuario.setPassword(passwordEncriptada);
            usuarioPersistence.save(usuario);
            return true;
        }
        public List<Usuario> obtenerTodosUsuarios() { return usuarioPersistence.findAll(); }
        public boolean esPropietario(Long usuarioId) { Usuario u = obtenerPorId(usuarioId); return u != null && u.getRol() == Usuario.Rol.PROPIETARIO; }
        public boolean esInquilino(Long usuarioId) { Usuario u = obtenerPorId(usuarioId); return u != null && u.getRol() == Usuario.Rol.INQUILINO; }
        public List<Usuario> obtenerUsuariosPorRol(Usuario.Rol rol) { return usuarioPersistence.findByRol(rol); }
    }
}