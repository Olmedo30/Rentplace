// src/main/java/es/uclm/rentplace/controller/PagoController.java
package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Pago;
import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.service.PagoService;
import es.uclm.rentplace.service.ReservaService;
import es.uclm.rentplace.persistence.usuarioDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/pagos")
public class PagoController {
    
    @Autowired
    private PagoService pagoService;
    
    @Autowired
    private ReservaService reservaService;
    
    @Autowired
    private usuarioDAO usuarioDAO;
    
    @GetMapping("/pagar/{reservaId}")
    public String mostrarFormularioPago(@PathVariable Long reservaId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "redirect:/login";
        }
        
        Reserva reserva = reservaService.obtenerReservaPorId(reservaId);
        if (reserva == null) {
            model.addAttribute("error", "Reserva no encontrada.");
            return "redirect:/home";
        }
        
        if (!reserva.getInquilino().getId().equals(userId)) {
            model.addAttribute("error", "No tienes permisos para pagar esta reserva.");
            return "redirect:/home";
        }
        
        if (reserva.getPagado()) {
            model.addAttribute("error", "Esta reserva ya ha sido pagada.");
            return "redirect:/mis-reservas";
        }
        
        model.addAttribute("reserva", reserva);
        model.addAttribute("metodosPago", Pago.MetodoPago.values());
        return "formulario-pago";
    }
    
    @PostMapping("/procesar/{reservaId}")
    public String procesarPago(
            @PathVariable Long reservaId,
            @RequestParam Pago.MetodoPago metodoPago,
            HttpSession session,
            Model model) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario inquilino = usuarioDAO.findById(userId).orElse(null);
        Reserva reserva = reservaService.obtenerReservaPorId(reservaId);
        
        if (inquilino == null || reserva == null) {
            model.addAttribute("error", "Usuario o reserva no encontrados.");
            return "redirect:/home";
        }
        
        if (!reserva.getInquilino().getId().equals(userId)) {
            model.addAttribute("error", "No tienes permisos para pagar esta reserva.");
            return "redirect:/home";
        }
        
        BigDecimal montoTotal = pagoService.calcularTotalReserva(reserva);
        
        Pago pago = pagoService.crearPago(reserva, montoTotal, metodoPago);
        pagoService.completarPago(pago.getId());
        
        if (reserva.getPropiedad().getPermiteReservaInmediata()) {
            reservaService.confirmarReserva(reserva.getId());
        }
        
        model.addAttribute("message", "Pago realizado exitosamente por " + montoTotal + "€.");
        return "redirect:/mis-reservas";
    }
    
    @GetMapping("/historial")
    public String verHistorialPagos(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        List<Pago> pagos;
        if (usuario.getRol() == Usuario.Rol.INQUILINO) { 
            pagos = pagoService.obtenerPagosDeInquilino(userId);
        } else if (usuario.getRol() == Usuario.Rol.PROPIETARIO) {
            pagos = pagoService.obtenerPagosDePropietario(userId);
        } else {
            pagos = java.util.Collections.emptyList();
        }
        
        model.addAttribute("pagos", pagos);
        return "historial-pagos";
    }
    
    @GetMapping("/detalle/{pagoId}")
    public String verDetallePago(@PathVariable Long pagoId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Pago pago = pagoService.obtenerPagoPorId(pagoId);
        if (pago == null) {
            model.addAttribute("error", "Pago no encontrado.");
            return "redirect:/pagos/historial";
        }
        
        boolean tienePermiso = 
         (pago.getReserva().getInquilino().getId().equals(userId)) || (pago.getReserva().getPropiedad().getPropietario().getId().equals(userId)); 
            
        
        
        if (!tienePermiso) {
            model.addAttribute("error", "No tienes permisos para ver este pago.");
            return "redirect:/pagos/historial";
        }
        
        model.addAttribute("pago", pago);
        return "detalle-pago";
    }
    
    @PostMapping("/reembolsar/{pagoId}")
    public String procesarReembolso(@PathVariable Long pagoId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        Pago pago = pagoService.obtenerPagoPorId(pagoId);
        if (pago == null) {
            model.addAttribute("error", "Pago no encontrado.");
            return "redirect:/pagos/historial";
        }
        
        pagoService.reembolsarPago(pagoId);
        model.addAttribute("message", "Reembolso procesado exitosamente por " + pago.getMonto() + "€.");
        return "redirect:/pagos/historial";
    }
    
    @GetMapping("/exito")
    public String pagoSuccess(@RequestParam("session_id") String sessionId, Model model) {
        try {
            Long reservaId = pagoService.confirmPaymentAndReserva(sessionId);
            if (reservaId != null) {
                model.addAttribute("message", "¡Reserva confirmada! ID de Reserva: " + reservaId);
                return "redirect:/mis-reservas";
            }
        } catch (Exception e) { 
            model.addAttribute("error", "Error de verificación de pago. Contacte con soporte.");
        }
        return "redirect:/home";
    }
    
    @GetMapping("/cancelado")
    public String pagoCancelled(@RequestParam Long reservaId, Model model) {
        model.addAttribute("error", "El pago ha sido cancelado. Su reserva (ID: " + reservaId + ") sigue en estado pendiente.");
        return "redirect:/mis-reservas";
    }
}