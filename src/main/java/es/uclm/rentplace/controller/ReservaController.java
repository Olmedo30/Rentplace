// src/main/java/es/uclm/rentplace/controller/ReservaController.java
package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Notificacion;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.ReservaDAO;
import es.uclm.rentplace.persistence.usuarioDAO;
import es.uclm.rentplace.service.PagoService;
import es.uclm.rentplace.service.PropiedadService;
import es.uclm.rentplace.service.NotificacionService;
import es.uclm.rentplace.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reservas")
public class ReservaController {
	private static final String ATTR_ERROR = "error";
	private static final String REDIRECT_PROPIEDADES = "redirect:/propiedades/";
    
    @Autowired
    private ReservaService reservaService;
    
    @Autowired
    private PropiedadService propiedadService;
    
    @Autowired
    private NotificacionService notificacionService;
    
    @Autowired
    private ReservaDAO reservaDAO;
    
    @Autowired
    private usuarioDAO usuarioDAO;
    
    @Autowired 
    private PagoService pagoService;
    
 // Reemplazar tu método procesarReserva actual por este:

    @PostMapping("/reservar")
    public String procesarReserva(
            @RequestParam Long alojamientoId,
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Usuario inquilino = usuarioDAO.findById(userId).orElse(null);
        if (inquilino == null) {
            model.addAttribute(ATTR_ERROR, "Debes iniciar sesión para reservar.");
            return "redirect:/login";
        }

        try {
            LocalDateTime inicio = LocalDate.parse(fechaEntrada).atStartOfDay();
            LocalDateTime fin = LocalDate.parse(fechaSalida).atStartOfDay();

            if (!fin.isAfter(inicio) || !inicio.isAfter(LocalDateTime.now())) {
                model.addAttribute(ATTR_ERROR, "Fechas no válidas.");
                return REDIRECT_PROPIEDADES + alojamientoId;
            }

            // Obtener la propiedad
            Propiedad propiedad = propiedadService.obtenerPorId(alojamientoId);
            if (propiedad == null) {
                model.addAttribute(ATTR_ERROR, "La propiedad no existe.");
                return REDIRECT_PROPIEDADES + alojamientoId;
            }
            
            if (propiedad.getPropietario().getId().equals(inquilino.getId())) {
                model.addAttribute(ATTR_ERROR, "No puedes reservar tu propia propiedad.");
                return REDIRECT_PROPIEDADES + alojamientoId;
            }

            // ✅ Verificar disponibilidad según el tipo de reserva
            boolean disponible;
            boolean esInmediata = Boolean.TRUE.equals(propiedad.getPermiteReservaInmediata());
            
            if (esInmediata) {
                disponible = reservaService.estaDisponibleInmediata(alojamientoId, inicio, fin);
            } else {
                disponible = reservaService.estaDisponibleNoInmediata(alojamientoId, inicio, fin);
            }

            if (!disponible) {
                model.addAttribute(ATTR_ERROR, "❌ Las fechas seleccionadas no están disponibles.");
                return REDIRECT_PROPIEDADES + alojamientoId;
            }

            // ✅ Crear la reserva
            Reserva nuevaReserva = reservaService.crearReservaConEstado(
            	    inquilino, 
            	    propiedad,
            	    inicio, 
            	    fin, 
            	    Reserva.PoliticaCancelacion.NO_REEMBOLSABLE
            );

            // ✅ Notificaciones según el tipo
            if (esInmediata) {
                // Notificar al inquilino (reserva confirmada)
                notificacionService.crearNotificacion(
                    inquilino, 
                    "Reserva confirmada", 
                    "Tu reserva para '" + propiedad.getTitulo() + "' del " + 
                    fechaEntrada + " al " + fechaSalida + " ha sido confirmada.", 
                    "RESERVA"
                );
                model.addAttribute("message", "✅ ¡Reserva confirmada! Las fechas están bloqueadas.");
            } else {
            	String mensaje = "El inquilino " + inquilino.getUsername() + 
                        " quiere reservar tu propiedad '" + propiedad.getTitulo() + "' del " + 
                        fechaEntrada + " al " + fechaSalida + ".";
        
        // Notificar al propietario (solicitud pendiente)
        Notificacion notif = new Notificacion(
            propiedad.getPropietario(), "Nueva solicitud de reserva", mensaje, "SOLICITUD_RESERVA");
        		notif.setReserva(nuevaReserva);
        		notificacionService.crearNotificacion(notif); 
        		model.addAttribute("message", "✅ Solicitud enviada. Esperando confirmación del propietario.");
            	}

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute(ATTR_ERROR, "❌ Error al procesar la reserva.");
        }

        return REDIRECT_PROPIEDADES + alojamientoId;
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
    
    @PostMapping("/confirmar-reserva/{reservaId}")
    public String confirmarReserva(@PathVariable Long reservaId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        Reserva reserva = reservaService.obtenerReservaPorId(reservaId);
        
        if (reserva == null || userId == null || 
            !reserva.getPropiedad().getPropietario().getId().equals(userId)) {
            model.addAttribute(ATTR_ERROR, "No tienes permiso para confirmar esta reserva.");
            return "redirect:/notificaciones";
        }

        reserva.setReservaConfirmada(true);
        reservaDAO.save(reserva);
        
        // Notificar al inquilino
        notificacionService.crearNotificacion(
            reserva.getInquilino(),
            "Reserva confirmada",
            "El propietario ha confirmado tu reserva para '" + reserva.getPropiedad().getTitulo() + "'.",
            "RESERVA"
        );
        
        model.addAttribute("message", "✅ Reserva confirmada exitosamente.");
        return "redirect:/notificaciones";
    }

    @PostMapping("/rechazar-reserva/{reservaId}")
    public String rechazarReserva(@PathVariable Long reservaId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        Reserva reserva = reservaService.obtenerReservaPorId(reservaId);
        
        if (reserva == null || userId == null || 
            !reserva.getPropiedad().getPropietario().getId().equals(userId)) {
            model.addAttribute(ATTR_ERROR, "No tienes permiso para rechazar esta reserva.");
            return "redirect:/notificaciones";
        }

        reserva.setReservaConfirmada(false); // O podrías eliminarla, según tu lógica
        reservaDAO.save(reserva);
        
        // Notificar al inquilino
        notificacionService.crearNotificacion(
            reserva.getInquilino(),
            "Reserva rechazada",
            "El propietario ha rechazado tu solicitud de reserva para '" + reserva.getPropiedad().getTitulo() + "'.",
            "RESERVA"
        );
        
        model.addAttribute("message", "❌ Reserva rechazada.");
        return "redirect:/notificaciones";
    }
}