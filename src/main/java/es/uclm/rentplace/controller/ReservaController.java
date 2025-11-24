package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.ReservaDAO;
import es.uclm.rentplace.persistence.usuarioDAO;
import es.uclm.rentplace.service.PagoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

@Controller
public class ReservaController {

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
        
        // Simulación de lógica de disponibilidad y precio
        if (fechaEntrada.isAfter(fechaSalida) || fechaEntrada.isBefore(LocalDate.now())) {
            model.addAttribute("error", "Fechas no válidas.");
            return "alojamiento-detalle";
        }
        
        // Crear la reserva en estado PENDIENTE, a la espera del pago
        Reserva nuevaReserva = new Reserva(usuario, alojamientoId, fechaEntrada, fechaSalida, precioTotal);
        Reserva savedReserva = reservaDAO.save(nuevaReserva);

        try {
            // 3. Iniciar el proceso de pago simulado
            String paymentUrl = pagoService.createCheckoutSession(savedReserva); // Uso del servicio renombrado
            
            // 4. Redirigir al usuario a la página de pago simulada (successUrl)
            return "redirect:" + paymentUrl;
            
        } catch (Exception e) { 
            System.err.println("Error al iniciar la simulación de pago: " + e.getMessage());
            model.addAttribute("error", "Error en el proceso de pago. Inténtelo de nuevo.");
            return "alojamiento-detalle"; 
        }
    }
}