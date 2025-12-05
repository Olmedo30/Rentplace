package es.uclm.rentplace.controller;

import es.uclm.rentplace.service.NotificacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private NotificacionService notificacionService;
    
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }
    
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        String rol = (String) session.getAttribute("rol");
        
        model.addAttribute("username", username);
        model.addAttribute("rol", rol);
        
        if (userId != null) {
            long notificacionesNoLeidas = notificacionService.contarNotificacionesNoLeidas(userId);
            model.addAttribute("notificacionesNoLeidas", notificacionesNoLeidas);
        }
        
        return "home";
    }
}