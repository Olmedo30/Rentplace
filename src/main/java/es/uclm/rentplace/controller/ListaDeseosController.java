package es.uclm.rentplace.controller;

import es.uclm.rentplace.service.ListaDeseosService;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lista-deseos")
public class ListaDeseosController {
    
    @Autowired
    private ListaDeseosService listaDeseosService;
    
    @Autowired
    private usuarioDAO usuarioDAO;
    
    // Mostrar lista de deseos
    @GetMapping
    public String verListaDeseos(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
    }
        
     // Verificar que es inquilino
        if (usuario.getRol() != Usuario.Rol.INQUILINO) {
            model.addAttribute("error", "Solo los inquilinos tienen lista de deseos.");
            return "profile";
        }
        
        model.addAttribute("propiedades", listaDeseosService.obtenerPropiedadesDeLista(userId));
        return "lista-deseos";
    }
    
    // Agregar propiedad a lista de deseos
    @PostMapping("/agregar/{propiedadId}")
    public String agregarPropiedad(@PathVariable Long propiedadId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null || usuario.getRol() != Usuario.Rol.INQUILINO) {
            model.addAttribute("error", "Solo los inquilinos pueden tener lista de deseos.");
            return "redirect:/propiedades/" + propiedadId;
        }
        
        if (listaDeseosService.agregarPropiedadALista(userId, propiedadId)) {
            model.addAttribute("message", "Propiedad agregada a tu lista de deseos.");
        } else {
            model.addAttribute("error", "No se pudo agregar la propiedad a tu lista de deseos.");
        }
        
        return "redirect:/propiedades/" + propiedadId;
    }
    
    // Eliminar propiedad de lista de deseos
    @PostMapping("/eliminar/{propiedadId}")
    public String eliminarPropiedad(@PathVariable Long propiedadId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null || usuario.getRol() != Usuario.Rol.INQUILINO) {
            model.addAttribute("error", "Solo los inquilinos pueden gestionar la lista de deseos.");
            return "redirect:/lista-deseos";
        }
        
        if (listaDeseosService.eliminarPropiedadDeLista(userId, propiedadId)) {
            model.addAttribute("message", "Propiedad eliminada de tu lista de deseos.");
        } else {
            model.addAttribute("error", "No se pudo eliminar la propiedad de tu lista de deseos.");
        }
        
        return "redirect:/lista-deseos";
    }
}