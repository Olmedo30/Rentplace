package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Notificacion;
import es.uclm.rentplace.service.NotificacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {
    
    @Autowired
    private NotificacionService notificacionService;
    
    // Mostrar todas las notificaciones
    @GetMapping
    public String verNotificaciones(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesDeUsuario(userId);
        long notificacionesNoLeidas = notificacionService.contarNotificacionesNoLeidas(userId);
        
        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("notificacionesNoLeidas", notificacionesNoLeidas);
        return "notificaciones";
    }
    
    // Marcar una notificación como leída
    @PostMapping("/marcar-leida/{id}")
    public String marcarComoLeida(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        notificacionService.marcarComoLeida(id);
        return "redirect:/notificaciones";
    }
    
    // Marcar todas las notificaciones como leídas
    @PostMapping("/marcar-todas-leidas")
    public String marcarTodasComoLeidas(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        notificacionService.marcarTodasComoLeidas(userId);
        return "redirect:/notificaciones";
    }
}