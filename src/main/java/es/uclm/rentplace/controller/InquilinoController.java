// src/main/java/es/uclm/rentplace/controller/InquilinoController.java
package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inquilino")
public class InquilinoController {

    @Autowired
    private usuarioDAO usuarioDAO;

    @PostMapping("/convertir")
    public String convertirAInquilino(HttpSession session, Model model) {
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

        // Cambiar el rol del usuario a INQUILINO
        usuario.setRol(Usuario.Rol.INQUILINO);
        usuarioDAO.save(usuario);

        // Actualizar la sesión
        session.setAttribute("rol", "INQUILINO");

        model.addAttribute("message", "Ahora eres inquilino. ¡Puedes buscar viviendas!");
        return "redirect:/profile";
    }
}