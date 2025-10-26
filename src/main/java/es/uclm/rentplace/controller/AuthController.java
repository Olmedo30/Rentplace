package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import es.uclm.rentplace.persistence.usuarioDAO;
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
        log.info("Usuarios existentes: {}", usuarioPersistence.findAll());
        return "register";
    }

    // Procesar registro
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String telefono,
            Model model) {

        Usuario nuevo = new Usuario(username, password, email, telefono);
        Usuario guardado = usuarioPersistence.save(nuevo);
        log.info("Usuario guardado: {}", guardado);
        model.addAttribute("mensaje", "Registro exitoso.");
        return "login";
    }

    // Mostrar login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Procesar login (versión simple sin autenticación real por ahora)
    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            Model model, HttpSession session) {
    	// Buscar al usuario por nombre de usuario
        var usuarioOpt = usuarioPersistence.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Comparar contraseñas en texto plano (solo para desarrollo)
            if (password.equals(usuario.getPassword())) {
                // Inicio de sesión exitoso: guardar en sesión
                session.setAttribute("username", usuario.getUsername());
                session.setAttribute("userId", usuario.getId());
                return "redirect:/home";
            }
        }

        // Credenciales incorrectas
        model.addAttribute("error", "Nombre de usuario o contraseña incorrectos.");
        return "login";
    }
}