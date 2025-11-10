// src/main/java/es/uclm/rentplace/service/UsuarioService.java
package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private usuarioDAO usuarioPersistence;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registrarUsuario(String username, String password, String email, String telefono) {
        if (usuarioPersistence.existsByUsername(username)) {
            return false; // ya existe
        }
        if (usuarioPersistence.existsByEmail(email)) {
            return false; // email ya usado
        }

        String passwordEncriptada = passwordEncoder.encode(password);
        Usuario usuario = new Usuario(username, passwordEncriptada, email, telefono);
        usuarioPersistence.save(usuario);
        return true;
    }

    public boolean autenticar(String username, String passwordPlana) {
        Usuario usuario = usuarioPersistence.findByUsername(username).orElse(null);
        if (usuario == null) {
            return false;
        }
        return passwordEncoder.matches(passwordPlana, usuario.getPassword());
    }
}