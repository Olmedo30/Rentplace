package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Propietario;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.PropietarioDAO;
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
public class PropietarioController {

    private static final Logger log = LoggerFactory.getLogger(PropietarioController.class);

    @Autowired
    private PropietarioDAO propietarioDAO;

    @Autowired
    private usuarioDAO usuarioDAO;

    @GetMapping("/become-propietario")
    public String showBecomePropietario(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "Debes iniciar sesión para convertirte en propietario.");
            return "login";
        }

        if (propietarioDAO.existsByUsuarioId(userId)) {
            model.addAttribute("error", "Ya eres propietario.");
            return "home";
        }

        model.addAttribute("propietario", new Propietario());
        return "become-propietario";
    }

    @PostMapping("/become-propietario")
    public String registerPropietario(
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

        if (propietarioDAO.existsByUsuarioId(userId)) {
            model.addAttribute("error", "Ya eres propietario.");
            return "home";
        }

        var usuarioOpt = usuarioDAO.findById(userId);
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "home";
        }

        Usuario usuario = usuarioOpt.get();
        Propietario propietario = new Propietario(nombre, email, telefono, usuario);
        propietarioDAO.save(propietario);

        log.info("Propietario registrado: {}", propietario);
        model.addAttribute("message", "Te has convertido en propietario exitosamente.");
        return "home";
    }
}