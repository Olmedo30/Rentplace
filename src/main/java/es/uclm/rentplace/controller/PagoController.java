package es.uclm.rentplace.controller;

import es.uclm.rentplace.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PagoController {

    @Autowired
    private PagoService pagoService;
 
    // Endpoint al que el servicio simulado redirige después de un PAGO EXITOSO
    @GetMapping("/pago/exito")
    public String pagoSuccess(@RequestParam("session_id") String sessionId, Model model) {
        try {
            // Confirmar el pago simulado y actualizar la reserva
            Long reservaId = pagoService.confirmPaymentAndReserva(sessionId);

            if (reservaId != null) {
                model.addAttribute("message", "¡Reserva confirmada! ID de Reserva: " + reservaId);
                return "home";
            } else {
                model.addAttribute("error", "Pago completado, pero la reserva no pudo ser confirmada (simulación fallida). Contacte con soporte.");
                return "home";
            }
        } catch (Exception e) { 
            model.addAttribute("error", "Error de verificación de pago. Contacte con soporte.");
            System.err.println("Error al verificar la sesión de pago: " + e.getMessage());
            return "home";
        }
    }

    // Endpoint al que el servicio simulado redirige si el usuario CANCELA el pago
    @GetMapping("/pago/cancelado")
    public String pagoCancelled(@RequestParam Long reservaId, Model model) { // La reserva permanece en estado PENDIENTE.
        model.addAttribute("error", "El pago ha sido cancelado. Su reserva (ID: " + reservaId + ") sigue en estado pendiente y puede ser eliminada.");
        return "home";
    }
}