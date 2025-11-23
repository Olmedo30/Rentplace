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
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inquilino")
public class InquilinoController {

    private static final Logger log = LoggerFactory.getLogger(InquilinoController.class);

    @Autowired
    private InquilinoDAO inquilinoDAO;

    @Autowired
    private usuarioDAO usuarioDAO;

    // Convertir usuario actual en inquilino
    @PostMapping("/convertir")
    public String convertirAInquilino(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "Debes iniciar sesión.");
            return "login";
        }

        if (inquilinoDAO.existsByUsuarioId(userId)) {
            model.addAttribute("message", "Ya eres inquilino.");
            return "home";
        }

        var usuarioOpt = usuarioDAO.findById(userId);
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "home";
        }

        Usuario usuario = usuarioOpt.get();
        Inquilino inquilino = new Inquilino(usuario);
        inquilinoDAO.save(inquilino);

        log.info("Usuario {} convertido en inquilino", usuario.getUsername());
        model.addAttribute("message", "Ahora eres inquilino. ¡Puedes buscar viviendas!");
        return "home";
    }

    // Vista opcional: página de inquilino
    @GetMapping("/perfil")
    public String verPerfilInquilino(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || !inquilinoDAO.existsByUsuarioId(userId)) {
            model.addAttribute("error", "Solo los inquilinos pueden acceder.");
            return "home";
        }
        return "perfil-inquilino"; // Puedes crear esta vista después
    }
}