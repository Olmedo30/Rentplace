// src/main/java/es/uclm/rentplace/controller/HomeController.java
package es.uclm.rentplace.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);
        return "home";
    }
 // Nueva ruta para el listado de todas las viviendas
    @GetMapping("/listado")
    public String listadoPropiedades(Model model) {
        // Esto se implementará en PropiedadController
        return "redirect:/propiedades/listado";
    }
    
    @PostMapping("/convertir-rol")
    public String convertirRol(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
        	return "redirect:/login";
        }
        
        // Esto se implementará en AuthController
        return "redirect:/auth/convertir-rol";
        }
    
    // Nueva ruta para el perfil del usuario
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        
        
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        return "profile";
    }
}