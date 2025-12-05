// src/main/java/es/uclm/rentplace/service/UsuarioService.java
package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private usuarioDAO usuarioPersistence;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registrar un nuevo usuario con todos los campos
    @Transactional
    public boolean registrarUsuario(String username, String password, String email, String telefono, 
                                    String nombre, String apellidos, String direccion, Usuario.Rol rol) {
        // Validar que los campos obligatorios no estén vacíos
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            telefono == null || telefono.trim().isEmpty() ||
            nombre == null || nombre.trim().isEmpty() ||
            apellidos == null || apellidos.trim().isEmpty() ||
            direccion == null || direccion.trim().isEmpty() ||
            rol == null) {
            return false;
        }

        // Validar existencia de username y email
        if (usuarioPersistence.existsByUsername(username)) {
            return false; // Username ya existe
        }
        if (usuarioPersistence.existsByEmail(email)) {
            return false; // Email ya usado
        }

        // Encriptar contraseña
        String passwordEncriptada = passwordEncoder.encode(password);
        
        // Crear y guardar usuario
        Usuario usuario = new Usuario(username, passwordEncriptada, email, telefono, nombre, apellidos, direccion, rol);
        usuarioPersistence.save(usuario);
        return true;
    }

    // Autenticar usuario
    public boolean autenticar(String username, String passwordPlana) {
        Optional<Usuario> usuarioOpt = usuarioPersistence.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        
        Usuario usuario = usuarioOpt.get();
        return passwordEncoder.matches(passwordPlana, usuario.getPassword());
    }
    
    // Obtener usuario por ID
    public Usuario obtenerPorId(Long id) {
        return usuarioPersistence.findById(id).orElse(null);
    }
    
    // Obtener usuario por username
    public Usuario obtenerPorUsername(String username) {
        return usuarioPersistence.findByUsername(username).orElse(null);
    }
    
    // Actualizar perfil de usuario (sin cambiar contraseña)
    @Transactional
    public boolean actualizarPerfil(Long id, String email, String telefono, String nombre, String apellidos, String direccion) {
        Usuario usuario = usuarioPersistence.findById(id).orElse(null);
        if (usuario == null) {
            return false;
        }
        
        // Validar email único (excepto para el mismo usuario)
        if (!usuario.getEmail().equals(email) && usuarioPersistence.existsByEmail(email)) {
            return false;
        }
        
        // Actualizar campos
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setDireccion(direccion);
        
        usuarioPersistence.save(usuario);
        return true;
    }
    
    // Cambiar contraseña (con validación de contraseña actual)
    @Transactional
    public boolean cambiarContrasena(Long id, String passwordActual, String nuevaPassword) {
        Usuario usuario = usuarioPersistence.findById(id).orElse(null);
        if (usuario == null) {
            return false;
        }
        
        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            return false;
        }
        
        // Encriptar nueva contraseña
        String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
        usuario.setPassword(passwordEncriptada);
        usuarioPersistence.save(usuario);
        return true;
    }
    
    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioPersistence.findAll();
    }
    
    // Verificar si un usuario es propietario
    public boolean esPropietario(Long usuarioId) {
        Usuario usuario = obtenerPorId(usuarioId);
        return usuario != null && usuario.getRol() == Usuario.Rol.PROPIETARIO;
    }
    
    // Verificar si un usuario es inquilino
    public boolean esInquilino(Long usuarioId) {
        Usuario usuario = obtenerPorId(usuarioId);
        return usuario != null && usuario.getRol() == Usuario.Rol.INQUILINO;
    }
    
    // Obtener usuarios por rol
    public List<Usuario> obtenerUsuariosPorRol(Usuario.Rol rol) {
        return usuarioPersistence.findByRol(rol);
    }
}