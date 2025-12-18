// src/main/java/es/uclm/rentplace/controller/ReservaController.java
package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.ReservaDAO;
import es.uclm.rentplace.persistence.usuarioDAO;
import es.uclm.rentplace.service.PagoService;
import es.uclm.rentplace.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reservas")
public class ReservaController {
    
    @Autowired
    private ReservaService reservaService;
    
    @Autowired
    private ReservaDAO reservaDAO;
    
    @Autowired
    private usuarioDAO usuarioDAO;
    
    @Autowired 
    private PagoService pagoService;
    
    @PostMapping("/reservar")
    public String procesarReserva(
            @RequestParam Long alojamientoId,
            @RequestParam LocalDate fechaEntrada,
            @RequestParam LocalDate fechaSalida,
            @RequestParam Double precioTotal,
            HttpSession session,
            Model model) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "Debes iniciar sesión para reservar.");
            return "login"; 
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            model.addAttribute("error", "Error: Usuario no encontrado.");
            return "home";
        }
        
        if (fechaEntrada.isAfter(fechaSalida) || fechaEntrada.isBefore(LocalDate.now())) {
            model.addAttribute("error", "Fechas no válidas.");
            return "alojamiento-detalle";
        }
        
        java.time.LocalDateTime fechaEntradaCompleta = fechaEntrada.atStartOfDay();
        java.time.LocalDateTime fechaSalidaCompleta = fechaSalida.atStartOfDay();
        
        Reserva nuevaReserva = reservaService.crearReserva(usuario, alojamientoId, fechaEntradaCompleta, fechaSalidaCompleta, Reserva.PoliticaCancelacion.NO_REEMBOLSABLE);
        
        model.addAttribute("message", "Solicitud de reserva creada exitosamente. Esperando confirmación del propietario.");
        return "redirect:/mis-reservas";
    }
    
    @GetMapping("/mis-reservas")
    public String listarMisReservas(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<Reserva> reservas = reservaService.obtenerReservasDelinquino(userId);
        model.addAttribute("reservas", reservas);
        return "mis-reservas";
    }
    
    @PostMapping("/confirmar-solicitud/{solicitudId}")
    public String confirmarSolicitud(@PathVariable Long solicitudId, HttpSession session, Model model) {
        if (reservaService.confirmarSolicitudReserva(solicitudId)) {
            model.addAttribute("message", "Solicitud de reserva confirmada exitosamente.");
        } else {
            model.addAttribute("error", "No se pudo confirmar la solicitud de reserva.");
        }
        return "redirect:/notificaciones";
    }
    
    @PostMapping("/rechazar-solicitud/{solicitudId}")
    public String rechazarSolicitud(@PathVariable Long solicitudId, HttpSession session, Model model) {
        if (reservaService.rechazarSolicitudReserva(solicitudId)) {
            model.addAttribute("message", "Solicitud de reserva rechazada exitosamente.");
        } else {
            model.addAttribute("error", "No se pudo rechazar la solicitud de reserva.");
        }
        return "redirect:/notificaciones";
    }
}