// src/main/java/es/uclm/rentplace/controller/AuthController.java
package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
	private static final String VIEW_REGISTER = "register";
    private static final String ATTR_ERROR = "error";

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private usuarioDAO usuarioPersistence;
    
    @Autowired
    private usuarioDAO usuarioDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String showSignup(Model model) {
        model.addAttribute("usuario", new Usuario());
        return VIEW_REGISTER;
    }

    // Procesar registro
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String rol,
            Model model) {

        // Validar contraseñas
    	   if (!password.equals(confirmPassword)) {
    	        model.addAttribute(ATTR_ERROR, "Las contraseñas no coinciden.");
    	        return VIEW_REGISTER;
    	}

        // Validar existencia
        if (usuarioPersistence.existsByUsername(username)) {
            model.addAttribute(ATTR_ERROR, "El nombre de usuario ya está en uso.");
            return VIEW_REGISTER;
        }
        if (usuarioPersistence.existsByEmail(email)) {
            model.addAttribute(ATTR_ERROR, "El correo electrónico ya está registrado.");
            return VIEW_REGISTER;
        }

        // Convertir rol
        Usuario.Rol rolEnum;
        try {
            rolEnum = Usuario.Rol.valueOf(rol);
        } catch (IllegalArgumentException e) {
            model.addAttribute(ATTR_ERROR, "Rol inválido.");
            return VIEW_REGISTER;
        }

        // Crear y guardar usuario
        Usuario nuevo = new Usuario(username, password, email, telefono, rolEnum);
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

        model.addAttribute(ATTR_ERROR, "Nombre de usuario o contraseña incorrectos.");
        return "login";
    }
    
    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    @PostMapping("/auth/convertir-rol")
    @Transactional
    public String convertirRol(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute(ATTR_ERROR, "Debes iniciar sesión.");
            return "login";
        }

        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            model.addAttribute(ATTR_ERROR, "Usuario no encontrado.");
            return "home";
        }

        // Alternar entre roles
        if (usuario.getRol() == Usuario.Rol.INQUILINO) {
            usuario.setRol(Usuario.Rol.PROPIETARIO);
            model.addAttribute("message", "¡Ahora eres propietario! Puedes añadir y gestionar tus propiedades.");
        } else {
            usuario.setRol(Usuario.Rol.INQUILINO);
            model.addAttribute("message", "¡Ahora eres inquilino! Puedes buscar y reservar propiedades.");
        }

        usuarioDAO.save(usuario);
        session.setAttribute("rol", usuario.getRol().name());
        
        return "redirect:/profile";
    }
}