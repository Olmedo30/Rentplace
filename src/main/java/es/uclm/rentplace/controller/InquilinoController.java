package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Inquilino;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.InquilinoDAO;
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
public class InquilinoController {

    private static final Logger log = LoggerFactory.getLogger(InquilinoController.class);

    @Autowired
    private InquilinoDAO inquilinoDAO;

    @Autowired
    private usuarioDAO usuarioDAO;

    // Mostrar formulario para convertirse en inquilino
    @GetMapping("/become-inquilino")
    public String showBecomeInquilino(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "Debes iniciar sesión para convertirte en inquilino.");
            return "login";
        }

        // Verificar si ya es inquilino
        if (inquilinoDAO.existsByUsuarioId(userId)) {
            model.addAttribute("error", "Ya eres inquilino.");
            return "home";
        }

        model.addAttribute("inquilino", new Inquilino());
        return "become-inquilino";
    }

    // Procesar la solicitud de convertirse en inquilino
    @PostMapping("/become-inquilino")
    public String registerInquilino(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String telefono,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "Sesión expirada. Inicia sesión de nuevo.");
            return "login";
        }

        // Verificar si ya es inquilino
        if (inquilinoDAO.existsByUsuarioId(userId)) {
            model.addAttribute("error", "Ya eres inquilino.");
            return "home";
        }

        // Obtener el usuario desde la sesión
        var usuarioOpt = usuarioDAO.findById(userId);
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "home";
        }

        Usuario usuario = usuarioOpt.get();

        // Crear y guardar el inquilino
        Inquilino inquilino = new Inquilino(nombre, email, telefono, usuario);
        inquilinoDAO.save(inquilino);

        log.info("Inquilino registrado: {}", inquilino);

        model.addAttribute("message", "Te has convertido en inquilino exitosamente.");
        return "home";
    }
}