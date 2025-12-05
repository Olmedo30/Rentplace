// src/main/java/es/uclm/rentplace/controller/AuthController.java
package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private usuarioDAO usuarioPersistence;

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String showSignup(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    // Procesar registro
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String direccion,
            @RequestParam String rol,
            Model model) {

        // Validar contraseñas
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }

        // Validar existencia
        if (usuarioPersistence.existsByUsername(username)) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "register";
        }
        if (usuarioPersistence.existsByEmail(email)) {
            model.addAttribute("error", "El correo electrónico ya está registrado.");
            return "register";
        }

        // Convertir rol
        Usuario.Rol rolEnum;
        try {
            rolEnum = Usuario.Rol.valueOf(rol);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Rol inválido.");
            return "register";
        }

        // Crear y guardar usuario
        Usuario nuevo = new Usuario(username, password, email, telefono, nombre, apellidos, direccion, rolEnum);
        usuarioPersistence.save(nuevo);
        log.info("Usuario guardado: {}", nuevo);
        model.addAttribute("message", "Registro exitoso. Ahora puedes iniciar sesión.");
        return "login";
    }

    // Mostrar login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Procesar login
    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        var usuarioOpt = usuarioPersistence.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (password.equals(usuario.getPassword())) {
                session.setAttribute("username", usuario.getUsername());
                session.setAttribute("userId", usuario.getId());
                session.setAttribute("rol", usuario.getRol().name());
                return "redirect:/home";
            }
        }

        model.addAttribute("error", "Nombre de usuario o contraseña incorrectos.");
        return "login";
    }
    
    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}