// src/main/java/es/uclm/rentplace/controller/NotificacionControllerAdvice.java
package es.uclm.rentplace.controller;

import es.uclm.rentplace.service.NotificacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NotificacionControllerAdvice {

    @Autowired
    private NotificacionService notificacionService;

    @ModelAttribute
    public void addNotificacionesNoLeidas(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            long notificacionesNoLeidas = notificacionService.contarNotificacionesNoLeidas(userId);
            model.addAttribute("notificacionesNoLeidas", notificacionesNoLeidas);
        }
    }
}