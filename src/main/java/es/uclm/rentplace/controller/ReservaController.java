package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import es.uclm.rentplace.service.ListaDeseosService;
import es.uclm.rentplace.service.PropiedadService;
import es.uclm.rentplace.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reservas")
public class ReservaController {
    
    @Autowired
    private ReservaService reservaService;
    
    @Autowired
    private PropiedadService propiedadService;
    
    @Autowired
    private usuarioDAO usuarioDAO;
    
    // Mostrar formulario de reserva
    @GetMapping("/reservar/{propiedadId}")
    public String mostrarFormularioReserva(@PathVariable Long propiedadId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Propiedad propiedad = propiedadService.obtenerPorId(propiedadId);
        if (propiedad == null || !propiedad.getActivo()) {
            model.addAttribute("error", "Propiedad no encontrada o no disponible.");
            return "error";
        }
        
        model.addAttribute("propiedad", propiedad);
        model.addAttribute("reserva", new Reserva());
        return "formulario-reserva";
    }
    
    // Procesar reserva
    @PostMapping("/reservar/{propiedadId}")
    public String crearReserva(
            @PathVariable Long propiedadId,
            @RequestParam LocalDateTime fechaEntrada,
            @RequestParam LocalDateTime fechaSalida,
            @RequestParam Reserva.PoliticaCancelacion politicaCancelacion,
            HttpSession session,
            Model model) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario inquilino = usuarioDAO.findById(userId).orElse(null);
        if (inquilino == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "redirect:/login";
        }
        
        // Verificar que es inquilino
        if (inquilino.getRol() != Usuario.Rol.INQUILINO) {
            model.addAttribute("error", "Solo los inquilinos pueden realizar reservas.");
            return "redirect:/home";
        }
        
        Reserva reserva = reservaService.crearReserva(
            inquilino, propiedadId, fechaEntrada, fechaSalida, politicaCancelacion
        );
        
        if (reserva == null) {
            model.addAttribute("error", "No se pudo crear la reserva. La propiedad podría no estar disponible en esas fechas.");
            return "redirect:/propiedades/" + propiedadId;
        }
        
        // Si es reserva inmediata, redirigir a pago
        if (reserva.getPropiedad().getPermiteReservaInmediata()) {
            return "redirect:/pagos/pagar/" + reserva.getId();
        }
        
        model.addAttribute("message", "Solicitud de reserva creada exitosamente. Esperando confirmación del propietario.");
        return "redirect:/mis-reservas";
    }
    
    // Listar mis reservas
    @GetMapping("/mis-reservas")
    public String listarMisReservas(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<Reserva> reservas = reservaService.obtenerReservasDeInquilino(userId);
        model.addAttribute("reservas", reservas);
        return "mis-reservas";
    }
    
    // Confirmar solicitud de reserva (para propietarios)
    @PostMapping("/confirmar-solicitud/{solicitudId}")
    public String confirmarSolicitud(@PathVariable Long solicitudId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Verificar que es propietario y tiene permisos
        // ... lógica de verificación
        
        if (reservaService.confirmarSolicitudReserva(solicitudId)) {
            model.addAttribute("message", "Solicitud de reserva confirmada exitosamente.");
        } else {
            model.addAttribute("error", "No se pudo confirmar la solicitud de reserva.");
        }
        
        return "redirect:/notificaciones";
    }
    
    // Rechazar solicitud de reserva (para propietarios)
    @PostMapping("/rechazar-solicitud/{solicitudId}")
    public String rechazarSolicitud(@PathVariable Long solicitudId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Verificar que es propietario y tiene permisos
        // ... lógica de verificación
        
        if (reservaService.rechazarSolicitudReserva(solicitudId)) {
            model.addAttribute("message", "Solicitud de reserva rechazada exitosamente.");
        } else {
            model.addAttribute("error", "No se pudo rechazar la solicitud de reserva.");
        }
        
        return "redirect:/notificaciones";
    }
}