// src/main/java/es/uclm/rentplace/controller/PagoController.java
package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Pago;
import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.service.NotificacionService;
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
    private NotificacionService notificacionService;
    
    @Autowired
    private usuarioDAO usuarioDAO;
    
    // Mostrar formulario de pago
    @GetMapping("/pagar/{reservaId}")
    public String mostrarFormularioPago(@PathVariable Long reservaId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null || usuario.getRol() != Usuario.Rol.INQUILINO) {
            model.addAttribute("error", "No tienes permisos para realizar pagos.");
            return "redirect:/home";
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
    
    // Procesar pago
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
        
        // Simular pago (en producción esto se haría con una pasarela de pago)
        boolean pagoExitoso = true;
        
        if (pagoExitoso) {
            // Crear y completar el pago
            Pago pago = pagoService.crearPago(reserva, montoTotal, metodoPago);
            pagoService.completarPago(pago.getId());
            
            // Confirmar reserva si es inmediata
            if (reserva.getPropiedad().getPermiteReservaInmediata()) {
                reservaService.confirmarReserva(reserva.getId());
            }
            
            // Notificar al inquilino
            notificacionService.crearNotificacion(
                inquilino, 
                "Pago realizado", 
                "Tu pago de " + montoTotal + "€ para la reserva " + reservaId + " ha sido completado.",
                "PAGO"
            );
            
            // Notificar al propietario
            Usuario propietario = reserva.getPropiedad().getPropietario();
            notificacionService.crearNotificacion(
                propietario,
                "Nuevo pago recibido",
                "Se ha recibido un pago de " + montoTotal + "€ para tu propiedad: " + reserva.getPropiedad().getTitulo(),
                "PAGO"
            );
            
            model.addAttribute("message", "Pago realizado exitosamente por " + montoTotal + "€.");
            return "redirect:/mis-reservas";
        } else {
            model.addAttribute("error", "El pago falló. Por favor, intenta de nuevo.");
            return "redirect:/pagos/pagar/" + reservaId;
        }
    }
    
    // Endpoint para pago exitoso (simulación)
    @GetMapping("/exito")
    public String pagoSuccess(@RequestParam("session_id") String sessionId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            Long reservaId = pagoService.confirmPaymentAndReserva(sessionId);
            if (reservaId != null) {
                Reserva reserva = reservaService.obtenerReservaPorId(reservaId);
                if (reserva != null) {
                    Usuario usuario = usuarioDAO.findById(userId).orElse(null);
                    if (usuario != null) {
                        notificacionService.crearNotificacion(
                            usuario,
                            "Reserva confirmada",
                            "Tu reserva " + reservaId + " ha sido confirmada correctamente.",
                            "RESERVA"
                        );
                    }
                    
                    model.addAttribute("message", "¡Reserva confirmada! ID de Reserva: " + reservaId);
                    return "redirect:/mis-reservas";
                }
            }
            
            model.addAttribute("error", "Pago completado, pero la reserva no pudo ser confirmada. Contacte con soporte.");
            return "redirect:/home";
        } catch (Exception e) { 
            model.addAttribute("error", "Error de verificación de pago. Contacte con soporte.");
            System.err.println("Error al verificar la sesión de pago: " + e.getMessage());
            return "redirect:/home";
        }
    }
    
    // Endpoint para pago cancelado
    @GetMapping("/cancelado")
    public String pagoCancelled(@RequestParam Long reservaId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            Reserva reserva = reservaService.obtenerReservaPorId(reservaId);
            if (reserva != null) {
                Usuario usuario = usuarioDAO.findById(userId).orElse(null);
                if (usuario != null) {
                    notificacionService.crearNotificacion(
                        usuario,
                        "Pago cancelado",
                        "Tu pago para la reserva " + reservaId + " ha sido cancelado.",
                        "PAGO"
                    );
                }
                
                model.addAttribute("error", "El pago ha sido cancelado. Su reserva (ID: " + reservaId + ") sigue en estado pendiente.");
                return "redirect:/mis-reservas";
            }
        } catch (Exception e) {
            System.err.println("Error al procesar pago cancelado: " + e.getMessage());
        }
        
        model.addAttribute("error", "Error al procesar el pago cancelado. Contacte con soporte.");
        return "redirect:/home";
    }
    
    // Ver historial de pagos
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
            pagos = List.of();
        }
        
        model.addAttribute("pagos", pagos);
        return "historial-pagos";
    }
    
    // Ver detalle de un pago
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
        
        // Verificar permisos
        boolean tienePermiso = false;
        if (pago.getReserva().getInquilino().getId().equals(userId)) {
            tienePermiso = true;
        } else if (pago.getReserva().getPropiedad().getPropietario().getId().equals(userId)) {
            tienePermiso = true;
        }
        
        if (!tienePermiso) {
            model.addAttribute("error", "No tienes permisos para ver este pago.");
            return "redirect:/pagos/historial";
        }
        
        model.addAttribute("pago", pago);
        return "detalle-pago";
    }
    
    // Procesar reembolso
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
        
        // Solo administradores o propietarios pueden reembolsar
        if (usuario.getRol() != Usuario.Rol.PROPIETARIO && !usuario.getUsername().equals("admin")) {
            model.addAttribute("error", "No tienes permisos para realizar reembolsos.");
            return "redirect:/pagos/historial";
        }
        
        try {
            Pago pago = pagoService.obtenerPagoPorId(pagoId);
            if (pago == null) {
                model.addAttribute("error", "Pago no encontrado.");
                return "redirect:/pagos/historial";
            }
            
            // Verificar que el propietario sea el dueño de la propiedad
            if (usuario.getRol() == Usuario.Rol.PROPIETARIO && 
                !pago.getReserva().getPropiedad().getPropietario().getId().equals(userId)) {
                model.addAttribute("error", "No tienes permisos para reembolsar este pago.");
                return "redirect:/pagos/historial";
            }
            
            // Procesar reembolso
            pagoService.reembolsarPago(pagoId);
            
            // Notificar al inquilino
            Usuario inquilino = pago.getReserva().getInquilino();
            notificacionService.crearNotificacion(
                inquilino,
                "Reembolso procesado",
                "Se ha procesado un reembolso de " + pago.getMonto() + "€ para tu pago " + pagoId,
                "PAGO"
            );
            
            model.addAttribute("message", "Reembolso procesado exitosamente por " + pago.getMonto() + "€.");
            return "redirect:/pagos/historial";
        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar el reembolso: " + e.getMessage());
            return "redirect:/pagos/historial";
        }
    }
}